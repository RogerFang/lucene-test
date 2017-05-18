package analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.CharsRef;

import java.io.IOException;

/**
 * Created by Roger on 2017/3/1.
 */
public class MyAnalyzer extends Analyzer {

    // 自定义停用词
    private static final String[] stopWords = {"a","and", "of", "the", "to", "is", "that"};

    @Override
    protected TokenStreamComponents createComponents(String s) {
        // 分词器
        Tokenizer tokenizer = new StandardTokenizer();

        // 小写过滤器
        TokenFilter lowerCaseFilter = new LowerCaseFilter(tokenizer);
        // 同义词过滤器
        TokenFilter synonymFilter = new SynonymFilter(lowerCaseFilter, getSynonymMap(), true);
        // 停用词过滤器
        TokenFilter stopWordsFilter = new StopFilter(synonymFilter, buildCharArraySetFromArray(stopWords));
        // 词干过滤器
        TokenFilter stemFilter = new PorterStemFilter(stopWordsFilter);
        
        return new TokenStreamComponents(tokenizer, stemFilter);
    }

    
    private CharArraySet buildCharArraySetFromArray(String[] array) {
        CharArraySet set = new CharArraySet(array.length, true);
        for(String value : array) {
            set.add(value);
        }
        return set;
    }

    /**
     * 同义词
     * @return
     */
    private SynonymMap getSynonymMap() {
        String base1 = "fast";
        String syn1 = "speed";

        String base2 = "retrieval";
        String syn2 = "search";

        SynonymMap.Builder sb = new SynonymMap.Builder(true);
        sb.add(new CharsRef(base1), new CharsRef(syn1), true);
        sb.add(new CharsRef(base2), new CharsRef(syn2), true);
        SynonymMap smap = null;
        try {
            smap = sb.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return smap;
    }
}
