package com.lpf.domain.catalog;

import com.lpf.domain.model.GroupKind;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class DefaultCatalog {

    public record SeedLine(
            GroupKind groupKind,
            String groupName,
            int groupSort,
            String categoryName,
            int categorySort,
            String lineName,
            int lineSort
    ) {
    }

    private DefaultCatalog() {
    }

    public static List<SeedLine> lines() {
        return List.of(
                line(GroupKind.RECEITA, "RECEITA", 10, "RECEITAS", 10, "SALDO ANTERIOR", 10),
                line(GroupKind.RECEITA, "RECEITA", 10, "RECEITAS", 10, "SALÁRIO", 20),
                line(GroupKind.RECEITA, "RECEITA", 10, "RECEITAS", 10, "MESA 1", 30),
                line(GroupKind.RECEITA, "RECEITA", 10, "RECEITAS", 10, "MESA 2", 40),
                line(GroupKind.RECEITA, "RECEITA", 10, "RECEITAS", 10, "MESA 3", 50),
                line(GroupKind.RECEITA, "RECEITA", 10, "RECEITAS", 10, "REEMBOLSO IAM", 60),

                line(GroupKind.DESP_FIXA, "DESP. FIXA", 20, "HABITAÇÃO", 10, "AGUA - DAE", 10),
                line(GroupKind.DESP_FIXA, "DESP. FIXA", 20, "HABITAÇÃO", 10, "GÁS", 20),
                line(GroupKind.DESP_FIXA, "DESP. FIXA", 20, "HABITAÇÃO", 10, "ALUGUEL", 30),
                line(GroupKind.DESP_FIXA, "DESP. FIXA", 20, "HABITAÇÃO", 10, "IPTU", 40),
                line(GroupKind.DESP_FIXA, "DESP. FIXA", 20, "HABITAÇÃO", 10, "ENERGIA", 50),
                line(GroupKind.DESP_FIXA, "DESP. FIXA", 20, "HABITAÇÃO", 10, "INTERNET", 60),
                line(GroupKind.DESP_FIXA, "DESP. FIXA", 20, "EDUCAÇÃO", 20, "cursos", 10),
                line(GroupKind.DESP_FIXA, "DESP. FIXA", 20, "BELEZA", 30, null, 0),
                line(GroupKind.DESP_FIXA, "DESP. FIXA", 20, "SAUDE", 40, null, 0),
                line(GroupKind.DESP_FIXA, "DESP. FIXA", 20, "DIVERSOS", 50, "MEI", 10),
                line(GroupKind.DESP_FIXA, "DESP. FIXA", 20, "DIVERSOS", 50, "Emprestimo", 20),

                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "CARTÕES", 10, "CARTÃO DE CREDITO BRA", 10),
                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "CARTÕES", 10, "CARTÃO MP", 20),
                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "CARTÕES", 10, "CARTÃO ITAU", 30),
                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "ALIMENTAÇÃO", 20, "MERCADO", 10),
                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "ALIMENTAÇÃO", 20, "LEGACY COFFE", 20),
                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "ALIMENTAÇÃO", 20, "RESTAURANTE/ COMIDAS", 30),
                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "ALIMENTAÇÃO", 20, "RAÇÕES", 40),
                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "ALIMENTAÇÃO", 20, "GULOSEIMAS", 50),
                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "REMEDIOS/ SAÚDE", 30, "LANA", 10),
                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "REMEDIOS/ SAÚDE", 30, "LUCAS", 20),
                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "DIVERSOS", 40, "Lavanderia", 10),
                line(GroupKind.DESP_VAR, "DESP. VAR.", 30, "TRANSPORTE", 50, "UBER", 10)
        );
    }

    public static Map<String, BigDecimal> august2026Forecasts() {
        return Map.ofEntries(
                Map.entry("SALDO ANTERIOR", new BigDecimal("200.00")),
                Map.entry("SALÁRIO", new BigDecimal("4500.00")),
                Map.entry("MESA 1", new BigDecimal("216.00")),
                Map.entry("MESA 2", new BigDecimal("120.00")),
                Map.entry("MESA 3", new BigDecimal("60.00")),
                Map.entry("REEMBOLSO IAM", new BigDecimal("593.75")),
                Map.entry("ALUGUEL", new BigDecimal("2170.00")),
                Map.entry("INTERNET", new BigDecimal("100.00")),
                Map.entry("cursos", new BigDecimal("218.19")),
                Map.entry("MEI", new BigDecimal("86.90")),
                Map.entry("CARTÃO MP", new BigDecimal("290.67")),
                Map.entry("CARTÃO ITAU", new BigDecimal("1705.57")),
                Map.entry("Lavanderia", new BigDecimal("30.00"))
        );
    }

    private static SeedLine line(
            GroupKind kind,
            String groupName,
            int groupSort,
            String categoryName,
            int categorySort,
            String lineName,
            int lineSort
    ) {
        return new SeedLine(kind, groupName, groupSort, categoryName, categorySort, lineName, lineSort);
    }
}
