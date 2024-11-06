package ru.practicum.explorewithme.server.publicapi.compilations.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PublicCompilationServiceIntegrationTest {

    private final PublicCompilationService publicCompilationService;

    @Test
    public void checkSuccessGetCompilationsByPublicUser() {
        List<CompilationDto> compilationDtos = publicCompilationService.getCompilationsByPublicUser(Boolean.TRUE, 0, 10);

        assertThat(compilationDtos.size(), equalTo(1));

        CompilationDto compilationDto = compilationDtos.getFirst();
        checkCompilationDroResponse(compilationDto);
    }

    @Test
    public void checkSuccessGetCompilationByIdByPublicUser() {
        CompilationDto compilationDto = publicCompilationService.getCompilationByIdByPublicUser(1L);
        checkCompilationDroResponse(compilationDto);
    }

    private void checkCompilationDroResponse(CompilationDto compilationDto) {
        assertThat(compilationDto.getId(), equalTo(1L));
        assertThat(compilationDto.getPinned(), is(Boolean.TRUE));
        assertThat(compilationDto.getTitle(), equalTo("ТестоваяПодборка"));
        assertThat(compilationDto.getEvents(), notNullValue());
        assertThat(compilationDto.getEvents().size(), equalTo(1));

        EventShortDto eventShortDto = compilationDto.getEvents().getFirst();
        assertThat(eventShortDto.getId(), equalTo(2L));
        assertThat(eventShortDto.getTitle(), equalTo("Мегатитл"));
        assertThat(eventShortDto.getAnnotation(), equalTo("Для запросов"));
        assertThat(eventShortDto.getCategory(), notNullValue());
        assertThat(eventShortDto.getCategory().getId(), equalTo(2L));
        assertThat(eventShortDto.getCategory().getName(), equalTo("ТестКатегория1"));
        assertThat(eventShortDto.getConfirmedRequests(), equalTo(0));
        assertThat(eventShortDto.getEventDate(), equalTo("2024-10-01 23:59:59"));
        assertThat(eventShortDto.getInitiator(), notNullValue());
        assertThat(eventShortDto.getInitiator().getName(), equalTo("Name1"));
        assertThat(eventShortDto.getInitiator().getEmail(), equalTo("test1@test.ru"));
        assertThat(eventShortDto.getPaid(), is(Boolean.TRUE));
        assertThat(eventShortDto.getViews(), equalTo(0L));
    }
}
