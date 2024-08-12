package com.kh.dndncare.chating.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.dndncare.chating.model.vo.ChatingRoom;

@Mapper
public interface ChatingMapper {

	ChatingRoom getChatRoom(@Param("memberNo") int memberNo, @Param("matNo") int matNo);

}
