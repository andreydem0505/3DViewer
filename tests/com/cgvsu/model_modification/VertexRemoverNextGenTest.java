package com.cgvsu.model_modification;

import com.cgvsu.io.objreaderObsolete.ObjReaderObsolete;
import com.cgvsu.io.objwriter.ObjWriter;
import com.cgvsu.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class VertexRemoverNextGenTest {

    @Test
    void processModelRemoveAll() {
        String separator = System.lineSeparator();

        String inputFile = """
                v 1.0 1.0 1.0
                v 2.0 2.0 2.0
                v 3.0 3.0 3.0
                vt 0.1 0.2
                vt 0.3 0.4
                vn 0.0 0.0 1.0
                vn 1.0 0.0 0.0
                f 1/1/1 2/2/2 3/1/1
                f 2/2/2 3/1/1 1/1/1
                """;
        String expectedOutput = (
                "").trim();
        Model inputModel = ObjReaderObsolete.read(inputFile);

        VertexRemoverNextGen.processModel(inputModel, List.of(0, 1, 2), true, false, true, true, false);

        String inputRes = ObjWriter.formatOutput(inputModel, System.lineSeparator());
        Assertions.assertEquals(expectedOutput, inputRes);
    }

    @Test
    void processModelKeepVtAndVn() {
        String separator = System.lineSeparator();

        String inputFile = """
                v 1.0 1.0 1.0
                v 2.0 2.0 2.0
                v 3.0 3.0 3.0
                vt 0.1 0.2
                vt 0.3 0.4
                vn 0.0 0.0 1.0
                vn 1.0 0.0 0.0
                f 1/1/1 2/2/2 3/1/1
                f 2/2/2 3/1/1 1/1/1
                """;
        String expectedOutput = (
                "vt 0.1 0.2" + separator +
                        "vt 0.3 0.4" + separator +
                        "vn 0.0 0.0 1.0" + separator +
                        "vn 1.0 0.0 0.0").trim();
        Model inputModel = ObjReaderObsolete.read(inputFile);

        VertexRemoverNextGen.processModel(inputModel, List.of(0, 1, 2), false, true, false, false, false);

        String inputRes = ObjWriter.formatOutput(inputModel, System.lineSeparator());
        Assertions.assertEquals(expectedOutput, inputRes);
    }
    @Test
    void processModelKeepVn() {
        String separator = System.lineSeparator();

        String inputFile = """
                v 1.0 1.0 1.0
                v 2.0 2.0 2.0
                v 3.0 3.0 3.0
                vt 0.1 0.2
                vt 0.3 0.4
                vn 0.0 0.0 1.0
                vn 1.0 0.0 0.0
                f 1/1/1 2/2/2 3/1/1
                f 2/2/2 3/1/1 1/1/1
                """;
        String expectedOutput = (
                "vn 0.0 0.0 1.0" + separator +
                        "vn 1.0 0.0 0.0").trim();
        Model inputModel = ObjReaderObsolete.read(inputFile);

        VertexRemoverNextGen.processModel(inputModel, List.of(0, 1, 2), false, true, false, true, false);

        String inputRes = ObjWriter.formatOutput(inputModel, System.lineSeparator());
        Assertions.assertEquals(expectedOutput, inputRes);
    }
    @Test
    void processModelKeepVt() {
        String separator = System.lineSeparator();

        String inputFile = """
                v 1.0 1.0 1.0
                v 2.0 2.0 2.0
                v 3.0 3.0 3.0
                vt 0.1 0.2
                vt 0.3 0.4
                vn 0.0 0.0 1.0
                vn 1.0 0.0 0.0
                f 1/1/1 2/2/2 3/1/1
                f 2/2/2 3/1/1 1/1/1
                """;
        String expectedOutput = (
                "vt 0.1 0.2" + separator +
                        "vt 0.3 0.4").trim();
        Model inputModel = ObjReaderObsolete.read(inputFile);

        VertexRemoverNextGen.processModel(inputModel, List.of(0, 1, 2), false, true, true, false, false);

        String inputRes = ObjWriter.formatOutput(inputModel, System.lineSeparator());
        Assertions.assertEquals(expectedOutput, inputRes);
    }
}