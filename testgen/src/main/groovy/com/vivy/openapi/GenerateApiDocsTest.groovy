package com.vivy.openapi

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class GenerateApiDocsTest extends DefaultTask {

    def API_DOCS_TEST_OUT = "$project.projectDir/src/test/java/com/vivy/openapi"

    def API_DOCS_FILE_OUT = "build/api-docs.json";

    @Input
    String parentClass = null;

    @TaskAction
    def generate() {
        def names = parentClass.split("\\.")
        def parentClassSimpleName = names[names.length - 1];
        new File(API_DOCS_TEST_OUT).mkdirs()
        new File(API_DOCS_TEST_OUT, "OpenApiDocsTest.java").text =
            """package com.vivy.openapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import $parentClass;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenApiDocsTest extends $parentClassSimpleName {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Test
    void generateAPIDocs() throws Exception {
        var apiDocs = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertEquals(
                200,
                apiDocs.getStatusCodeValue(),
                "/v3/api-docs endpoint should return status code 200, looks like it is missing or misconfigured"
        );
        Files.writeString(Path.of("$API_DOCS_FILE_OUT"), apiDocs.getBody());
    }
}
"""
    }
}
