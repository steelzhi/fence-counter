package metalmarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import metalmarket.service.QueryService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class QueryController {
    private final QueryService testService;

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAnswer() throws IOException {
        return testService.getAnswer();
    }

/*    @GetMapping("/{city}")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAnswer(@PathVariable String city) throws IOException {
        return testService.getAnswer(String city);
    }*/


}