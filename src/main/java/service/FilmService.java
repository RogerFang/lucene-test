package service;

import dao.FilmMapper;
import model.Film;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created by Roger on 2017/2/28.
 */
public class FilmService {

    static FilmMapper filmMapper;
    
    static {
        String resource = "mybatis-config.xml";
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

        SqlSession session = factory.openSession();
        filmMapper = session.getMapper(FilmMapper.class);
    }
    
    public static List<Film> selectAll(){
        return filmMapper.selectAll();
    }
}
