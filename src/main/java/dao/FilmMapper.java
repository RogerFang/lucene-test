package dao;

import model.Film;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FilmMapper {
    
    @Select("select * from film")
    @Results({
            @Result(id = true, property = Film.FILM_ID, column = "film_id"),
            @Result(property = Film.LENGTH, column = "length"),
            @Result(property = Film.TITLE, column = "title"),
            @Result(property = Film.SPECIAL_FEATURES, column = "special_features"),
            @Result(property = Film.DESCRIPTION, column = "description")
    })
    List<Film> selectAll();
}