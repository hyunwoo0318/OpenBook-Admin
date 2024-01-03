package Project.OpenBook.Domain.Customer.Controller;

import Project.OpenBook.Domain.Customer.Dto.AdminDto;
import Project.OpenBook.Domain.Customer.Dto.CustomerNicknameDto;
import Project.OpenBook.Domain.Customer.Service.CustomerService;
import Project.OpenBook.Jwt.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 관리자 로그인
     * @param adminDto(아이디, 비밀번호)
     * @return
     */
    @Operation(summary = "관리자 로그인", description = "아이디와 비밀번호를 입력받아 관리자 로그인")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @PostMapping("/admin/login")
    public ResponseEntity<CustomerNicknameDto> adminLogin(@Validated @RequestBody AdminDto adminDto) {
        TokenDto tokenDto = customerService.loginAdmin(adminDto.getLoginId(), adminDto.getPassword());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenDto.getType() + " " + tokenDto.getAccessToken());
        headers.set("Refresh-Token", tokenDto.getRefreshToken());
        headers.setAccessControlAllowHeaders(Arrays.asList("Authorization", "Refresh-Token"));
        headers.setAccessControlExposeHeaders(Arrays.asList("Authorization", "Refresh-Token"));


        ResponseEntity<CustomerNicknameDto> responseEntity = ResponseEntity.ok()
                .headers(headers)
                .body(new CustomerNicknameDto(tokenDto.getNickname(), tokenDto.getIsNew()));

        return responseEntity;
    }

}
