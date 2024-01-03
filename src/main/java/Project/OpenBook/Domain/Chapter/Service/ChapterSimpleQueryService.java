package Project.OpenBook.Domain.Chapter.Service;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterDateDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterInfoDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterQueryAdminDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterTitleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterSimpleQueryService {

    private final ChapterRepository chapterRepository;
    private final ChapterValidator chapterValidator;

    public List<ChapterQueryAdminDto> queryChaptersAdmin() {
        return chapterRepository.findAll().stream()
                .map(c -> new ChapterQueryAdminDto(c.getTitle(), c.getNumber(), c.getDateComment(), c.getId()))
                .sorted(Comparator.comparing(ChapterQueryAdminDto::getNumber))
                .collect(Collectors.toList());
    }

    public ChapterTitleDto queryChapterTitle(Integer num) {
        return new ChapterTitleDto(chapterValidator.checkChapter(num).getTitle());
    }

    public ChapterDateDto queryChapterDate(Integer num) {
        Chapter chapter = chapterValidator.checkChapter(num);
        return new ChapterDateDto(chapter.getDateComment());
    }

    public ChapterInfoDto queryChapterInfo(Integer num) {
        return new ChapterInfoDto(chapterValidator.checkChapter(num).getContent());
    }


}
