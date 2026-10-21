package org.dstadler.poi.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.util.RecordFormatException;

import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

/**
 * Fuzz target for the Apache POI Formula Parser.
 * Used by Google's OSS-Fuzz for continuous security testing.
 */
public class FormulaParserFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        try {
            FormulaType formulaType = data.pickValue(FormulaType.values());
            int sheetIndex = data.consumeInt(-1, 10);
            String formula = data.consumeRemainingAsString();

            if (formula == null || formula.isEmpty()) {
                return;
            }

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFEvaluationWorkbook evalWorkbook = HSSFEvaluationWorkbook.create(workbook);
            FormulaParser.parse(formula, evalWorkbook, formulaType, sheetIndex);
        } catch (FormulaParseException | IllegalArgumentException | IllegalStateException |
                 IndexOutOfBoundsException | ArithmeticException | NegativeArraySizeException |
                 RecordFormatException | BufferUnderflowException |
                 UnsupportedOperationException | NoSuchElementException e) {
            // Expected exceptions on malformed formula syntax
        }
    }
}
