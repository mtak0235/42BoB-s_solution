package bobs.Dto;


import bobs.domain.CanceledRoom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityLogDto {
    int id;
    String activity_status;
    int location_id;
    String created_at;
    String user_id;

    public ActivityLogDto() {
    }

    public ActivityLogDto getRoomEnterLog(RoomInfoDto roomInfo, RoomMatchDto roomMatch) {
        ActivityLogDto logDto = new ActivityLogDto();
        logDto.setActivity_status("room_enter");
        logDto.setUser_id(roomMatch.getUser_id());
        logDto.setLocation_id(roomInfo.getLocation_id());

        return logDto;
    }

    public ActivityLogDto getLoginLog(SessionDto session) {
        ActivityLogDto logDto = new ActivityLogDto();
        logDto.setActivity_status("login");
        logDto.setUser_id(session.getUser_id());
        logDto.setLocation_id(session.getLocation_id());

        return logDto;
    }

    public ActivityLogDto getRoomExitLog(CanceledRoom canceledRoom, RoomInfoDto roomInfo) {
        ActivityLogDto logDto = new ActivityLogDto();
        logDto.setActivity_status("room_exit");
        logDto.setUser_id(canceledRoom.getUser_id());
        logDto.setLocation_id(roomInfo.getLocation_id());
        return logDto;
    }
}
