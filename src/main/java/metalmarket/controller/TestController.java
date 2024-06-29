package metalmarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import metalmarket.service.TestService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAnswer() throws IOException {
        return testService.getAnswer();
    }
}