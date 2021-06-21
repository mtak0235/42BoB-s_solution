package bobs.Service;

import bobs.Dto.RoomInfoDto;
import bobs.Dto.RoomMatchDto;

import java.util.ArrayList;
import java.util.List;

public interface RoomService {
	int roomCreate(RoomInfoDto roomInfoDTO);
	List<RoomInfoDto> findVaildRoom(RoomInfoDto roomInfoDto, RoomMatchDto roomMatchDto, String startTime, String endTime);
	boolean roomCountCheck(RoomMatchDto roomMatchDto);
	boolean userDupleCheck(RoomMatchDto roomMatchDto);
	int roomEnter(RoomMatchDto roomMatchDto);
}
