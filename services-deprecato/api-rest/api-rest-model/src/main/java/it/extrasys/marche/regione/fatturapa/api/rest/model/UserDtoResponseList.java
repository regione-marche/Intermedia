package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
        "userDtoList"
})
public class UserDtoResponseList {

    @JsonProperty ("user_dto_list")
    @JsonPropertyDescription ("")
    private List<UserDto> userDtoList;

    public UserDtoResponseList(){
        userDtoList = new ArrayList<>();
    }

    /*public void addUserDto(UserDto userDto){
        userDtoList.add(userDto);
    }*/

    @JsonProperty("user_dto_list")
    public List<UserDto> getUserDtoList() {
        return userDtoList;
    }

    @JsonProperty("user_dto_list")
    public void setUserDtoList(List<UserDto> userDtoList) {
        this.userDtoList = userDtoList;
    }
}
