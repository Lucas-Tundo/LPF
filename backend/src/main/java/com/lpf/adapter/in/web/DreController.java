package com.lpf.adapter.in.web;

import com.lpf.adapter.config.TransactionalUseCases;
import com.lpf.adapter.in.security.CurrentUser;
import com.lpf.adapter.in.web.dto.DreAssembler;
import com.lpf.adapter.in.web.dto.UpdateClosingRequest;
import com.lpf.adapter.in.web.dto.UpdateEntryRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DreController {

    private final TransactionalUseCases useCases;

    public DreController(TransactionalUseCases useCases) {
        this.useCases = useCases;
    }

    @GetMapping("/dre")
    public DreAssembler.DreMonthResponse month(
            @RequestParam(defaultValue = "2026") int year,
            @RequestParam(defaultValue = "8") int month
    ) {
        onboard();
        return DreAssembler.from(useCases.loadMonth(CurrentUser.id(), year, month));
    }

    @GetMapping("/dre/year/{year}")
    public List<DreAssembler.YearMonthResponse> year(@PathVariable int year) {
        onboard();
        return DreAssembler.fromYear(useCases.loadYear(CurrentUser.id(), year));
    }

    @PutMapping("/entries/{lineItemId}")
    public DreAssembler.DreMonthResponse updateEntry(
            @PathVariable UUID lineItemId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestBody UpdateEntryRequest request
    ) {
        onboard();
        return DreAssembler.from(useCases.updateEntry(
                CurrentUser.id(),
                lineItemId,
                year,
                month,
                request.forecast(),
                request.paidAmount()
        ));
    }

    @PutMapping("/months/{year}/{month}/closing")
    public DreAssembler.DreMonthResponse updateClosing(
            @PathVariable int year,
            @PathVariable int month,
            @RequestBody UpdateClosingRequest request
    ) {
        onboard();
        return DreAssembler.from(useCases.updateClosing(CurrentUser.id(), year, month, request.actualRemaining()));
    }

    private void onboard() {
        useCases.ensureReady(CurrentUser.id(), CurrentUser.email(), CurrentUser.displayName());
    }
}
