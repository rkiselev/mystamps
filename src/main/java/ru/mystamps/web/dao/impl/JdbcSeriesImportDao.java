/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.dao.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.SeriesImportDao;
import ru.mystamps.web.dao.dto.ImportRequestDto;
import ru.mystamps.web.dao.dto.ImportSeriesDbDto;
import ru.mystamps.web.dao.dto.ParsedDataDto;
import ru.mystamps.web.dao.dto.SaveParsedDataDbDto;

// it complains that "request_id" is present many times
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@RequiredArgsConstructor
public class JdbcSeriesImportDao implements SeriesImportDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${series_import_requests.create}")
	private String createSeriesImportRequestSql;
	
	@Value("${series_import_requests.change_status}")
	private String changeStatusSql;
	
	@Value("${series_import_requests.find_by_id}")
	private String findImportRequestByIdSql;
	
	@Value("${series_import_requests.add_raw_content}")
	private String addRawContentSql;
	
	@Value("${series_import_requests.find_raw_content_by_request_id}")
	private String findRawContentSql;
	
	@Value("${series_import_requests.add_parsed_content}")
	private String addParsedContentSql;
	
	@Value("${series_import_requests.find_parsed_data_by_request_id}")
	private String findParsedDataSql;
	
	@Override
	public Integer add(ImportSeriesDbDto importRequest) {
		Map<String, Object> params = new HashMap<>();
		params.put("url", importRequest.getUrl());
		params.put("status", importRequest.getStatus());
		params.put("updated_at", importRequest.getUpdatedAt());
		params.put("requested_at", importRequest.getRequestedAt());
		params.put("requested_by", importRequest.getRequestedBy());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			createSeriesImportRequestSql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding an import series request: %d",
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}
	
	// TODO: introduce dao
	@Override
	public void changeStatus(Integer requestId, Date date, String oldStatus, String newStatus) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", requestId);
		params.put("date", date);
		params.put("old_status", oldStatus);
		params.put("new_status", newStatus);
		
		int affected = jdbcTemplate.update(changeStatusSql, params);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after updating status of request #%d: %d",
			requestId,
			affected
		);
	}
	
	@Override
	public ImportRequestDto findById(Integer id) {
		try {
			return jdbcTemplate.queryForObject(
				findImportRequestByIdSql,
				Collections.singletonMap("id", id),
				RowMappers::forImportRequestDto
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	// TODO: introduce dao
	@Override
	public void addRawContent(Integer requestId, Date createdAt, Date updatedAt, String content) {
		Map<String, Object> params = new HashMap<>();
		params.put("request_id", requestId);
		params.put("created_at", createdAt);
		params.put("updated_at", updatedAt);
		params.put("content", content);
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addRawContentSql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding raw content to request #%d: %d",
			requestId,
			affected
		);
	}
	
	@Override
	public String findRawContentByRequestId(Integer requestId) {
		try {
			return jdbcTemplate.queryForObject(
				findRawContentSql,
				Collections.singletonMap("request_id", requestId),
				String.class
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public void addParsedContent(Integer requestId, SaveParsedDataDbDto data) {
		Map<String, Object> params = new HashMap<>();
		params.put("request_id", requestId);
		params.put("category_id", data.getCategoryId());
		params.put("country_id", data.getCountryId());
		params.put("image_url", data.getImageUrl());
		params.put("created_at", data.getCreatedAt());
		params.put("updated_at", data.getUpdatedAt());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addParsedContentSql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding parsed data to request #%d: %d",
			requestId,
			affected
		);
	}
	
	@Override
	public ParsedDataDto findParsedDataByRequestId(Integer requestId, String lang) {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("request_id", requestId);
			params.put("lang", lang);
			
			return jdbcTemplate.queryForObject(
				findParsedDataSql,
				params,
				RowMappers::forParsedDataDto
			);
			
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
}