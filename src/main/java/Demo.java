import model.Film;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;
import service.FilmService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Roger on 2017/2/28.
 */
public class Demo {

    private static StandardAnalyzer analyzer = new StandardAnalyzer();

    private static int hitsPerPage = 20;

    @Test
    public void testIndex() throws IOException {
        // 建索引
        index(analyzer);
    }

    @Test
    public void testSimpleQuery() throws ParseException, IOException {
        String queryStr = "Deleted Scenes";
        Query query = new QueryParser(Film.SPECIAL_FEATURES, analyzer).parse(queryStr);
        
        search(query);
    }
    
    @Test
    public void testFuzzyQuery() throws IOException, ParseException {
        FuzzyQuery query = new FuzzyQuery(new Term(Film.TITLE, "ACADEMY DINOSA"));
        search(query);
    }

    /**
     * 通配符查询
     * ? 表示一个任意字符
     * * 表示0或多个任意字符
     */
    @Test
    public void testWildcardQuery() throws IOException, ParseException {
        WildcardQuery query = new WildcardQuery(new Term(Film.TITLE, "ACA*AUR"));
        search(query);
    }
    
    @Test
    public void testBooleanQuery() throws IOException, ParseException {
        Query query1 = new QueryParser(Film.SPECIAL_FEATURES, analyzer).parse("Commentaries");
        Query query2 = NumericRangeQuery.newIntRange(Film.LENGTH, 60, 100, false, true);
        
        BooleanQuery booleanQuery = 
                new BooleanQuery.Builder()
                        .add(query1, BooleanClause.Occur.MUST)
                        .add(query2, BooleanClause.Occur.MUST)
                        .build();
        search(booleanQuery);
    }
    
    @Test
    public void testSort() throws ParseException, IOException {
        //构建排序字段  
        SortField[] sortField = new SortField[2];
        sortField[0] = new SortField(Film.LENGTH, SortField.Type.INT,true);
        sortField[1] = new SortField(Film.TITLE, SortField.Type.STRING,true);
        
        Sort sort = new Sort(sortField);
        
        String queryStr = "Deleted Scenes";
        Query query = new QueryParser(Film.SPECIAL_FEATURES, analyzer).parse(queryStr);

        search(query, sort);
    }
    
    @Test
    public void testAllTerms() throws IOException {
        IndexReader reader = DirectoryReader.open(getDirectory());
        Fields fields = MultiFields.getFields(reader);
        Terms terms = fields.terms(Film.DESCRIPTION);
        TermsEnum iterator = terms.iterator();
        while (iterator.next() != null){
            System.out.println(iterator.term().utf8ToString());
        }
    }
    
    private static Directory getDirectory() throws IOException {
        // in memory
        // Directory directory = new RAMDirectory();        
        // in file
        Directory directory = FSDirectory.open(Paths.get("D:\\lucene"));
        return directory;
    }

    /**
     * 建索引
     * @param analyzer
     * @throws IOException
     */
    public static void index(Analyzer analyzer) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(getDirectory(), config);
        addDocs(writer);
        writer.commit();
        writer.close();
    }
    
    private static void addDocs(IndexWriter writer) throws IOException {
        List<Film> films = FilmService.selectAll();
        for (Film film: films){
            addDoc(writer, film);
        }
    }

    private static void addDoc(IndexWriter writer, Film film) throws IOException {
        Document doc = new Document();
        doc.add(new IntField(Film.FILM_ID, film.getFilmId(), Field.Store.YES));
        doc.add(new StringField(Film.TITLE, film.getTitle(), Field.Store.YES));
        // 用于STRING类型排序
        doc.add(new SortedDocValuesField(Film.TITLE, new BytesRef(film.getTitle())));
        doc.add(new IntField(Film.LENGTH, film.getLength(), Field.Store.YES));
        // 用于NUMERIC排序检索
        doc.add(new NumericDocValuesField(Film.LENGTH, film.getLength()));
        doc.add(new TextField(Film.SPECIAL_FEATURES, film.getSpecialFeatures(), Field.Store.YES));
        doc.add(new TextField(Film.DESCRIPTION, film.getDescription(), Field.Store.YES));
        writer.addDocument(doc);
    }
    
    public static void search(Query query) throws ParseException, IOException {
        search(query, null);
    }

    public static void search(Query query, Sort sort) throws ParseException, IOException {
        IndexReader reader = DirectoryReader.open(getDirectory());

        IndexSearcher searcher = new IndexSearcher(reader);
        // searcher.setSimilarity(new BM25Similarity());

        TopDocs docs = sort == null ? searcher.search(query, hitsPerPage): searcher.search(query, hitsPerPage, sort);
        ScoreDoc[] hits = docs.scoreDocs;
        display(searcher, hits);
    }

    /**
     * 输出检索结果
     * @param searcher
     * @param hits
     * @throws IOException
     */
    private static void display(IndexSearcher searcher, ScoreDoc[] hits) throws IOException {
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get(Film.FILM_ID) + "\t" + d.get(Film.TITLE) + "\t" + d.get(Film.LENGTH) + "\t" + d.get(Film.SPECIAL_FEATURES) + "\t" + d.get(Film.DESCRIPTION));
        }
    }
}
