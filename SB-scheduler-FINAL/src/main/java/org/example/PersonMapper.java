package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PersonMapper implements RowMapper<Person>{

	@Override
	public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Person person = new Person();
		person.setPid(rs.getInt(1));
		person.setFirstName(rs.getString(2));
		person.setLastName(rs.getString(3));
		person.setStatus(rs.getString(4).charAt(0));
		return person;
	}
}
