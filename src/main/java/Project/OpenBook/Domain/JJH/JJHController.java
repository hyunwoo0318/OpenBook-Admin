package Project.OpenBook.Domain.JJH;

import Project.OpenBook.Domain.JJH.dto.JJHListAdminQueryDto;
import Project.OpenBook.Domain.JJH.dto.JJHListUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JJHController {

    private final JJHService jjhService;
    @Operation(summary = "관리자 페이지에서의 정주행 리스트 조회")
    @GetMapping("/admin/jjh")
    public ResponseEntity<JJHListAdminQueryDto> queryJJHAdmin() {
        JJHListAdminQueryDto listDto = jjhService.queryJJHAdmin();
        return new ResponseEntity<JJHListAdminQueryDto>(listDto, HttpStatus.OK);
    }

    @Operation(summary = "정주행 리스트 순서 변경")
    @PatchMapping("/admin/jjh")
    public ResponseEntity updateJJHList(@Validated @RequestBody JJHListUpdateDto dto) {
        jjhService.updateJJHList(dto);
        return new ResponseEntity( HttpStatus.OK);
    }

    @Operation(summary = "정주행 콘텐츠 업데이트")
    @PatchMapping("/admin/jjh/update")
    public ResponseEntity updateJJHContents() {
        jjhService.updateJJHContent();
        return new ResponseEntity( HttpStatus.OK);
    }



}
