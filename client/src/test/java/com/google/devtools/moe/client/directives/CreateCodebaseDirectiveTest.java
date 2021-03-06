/*
 * Copyright (c) 2011 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.moe.client.directives;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableSet;
import com.google.devtools.moe.client.SystemCommandRunner;
import com.google.devtools.moe.client.SystemFileSystem;
import com.google.devtools.moe.client.Ui;
import com.google.devtools.moe.client.codebase.ExpressionEngine;
import com.google.devtools.moe.client.project.ProjectContext;
import com.google.devtools.moe.client.repositories.Repositories;
import com.google.devtools.moe.client.repositories.RepositoryType;
import com.google.devtools.moe.client.testing.DummyRepositoryFactory;
import com.google.devtools.moe.client.testing.InMemoryProjectContextFactory;
import com.google.devtools.moe.client.testing.TestingUtils;
import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;

public class CreateCodebaseDirectiveTest extends TestCase {
  private final ByteArrayOutputStream stream = new ByteArrayOutputStream();
  private final Ui ui = new Ui(stream, /* fileSystem */ null);
  public final SystemCommandRunner cmd = new SystemCommandRunner();
  private final Repositories repositories =
      new Repositories(ImmutableSet.<RepositoryType.Factory>of(new DummyRepositoryFactory()));
  private final ExpressionEngine expressionEngine =
      TestingUtils.expressionEngineWithRepo(ui, new SystemFileSystem(), cmd);
  private final InMemoryProjectContextFactory contextFactory =
      new InMemoryProjectContextFactory(expressionEngine, ui, repositories);

  public void testCreateCodebase() throws Exception {
    contextFactory.projectConfigs.put(
        "moe_config.txt",
        "{\"name\": \"foo\", \"repositories\": {\"internal\": {\"type\": \"dummy\"}}}");
    ProjectContext context = contextFactory.create("moe_config.txt");
    CreateCodebaseDirective d = new CreateCodebaseDirective(context, cmd, ui, expressionEngine);
    d.codebase = "internal";
    assertEquals(0, d.perform());
    assertThat(stream.toString())
        .contains("Codebase \"internal\" created at /dummy/codebase/internal/1");
  }

  public void testCreateCodebaseWithEditors() throws Exception {
    contextFactory.projectConfigs.put(
        "moe_config.txt",
        "{\"name\": \"foo\", \"repositories\": {"
            + "\"internal\": {\"type\": \"dummy\"}}, \"editors\": {"
            + "\"identity\": {\"type\":\"identity\"}}}");
    ProjectContext context = contextFactory.create("moe_config.txt");
    CreateCodebaseDirective d = new CreateCodebaseDirective(context, cmd, ui, expressionEngine);
    d.codebase = "internal|identity";
    assertEquals(0, d.perform());
    assertThat(stream.toString())
        .contains("Codebase \"internal|identity\" created at /dummy/codebase/internal/1");

  }
}
