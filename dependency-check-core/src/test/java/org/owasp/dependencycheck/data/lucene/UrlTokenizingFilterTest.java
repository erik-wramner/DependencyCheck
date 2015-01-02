/*
 * This file is part of dependency-check-core.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2013 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.data.lucene;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;

/**
 *
 * @author Jeremy Long <jeremy.long@owasp.org>
 */
public class UrlTokenizingFilterTest extends BaseTokenStreamTestCase {

    private Analyzer analyzer;

    public UrlTokenizingFilterTest() {
        analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName,
                    Reader reader) {
                Tokenizer source = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
                return new TokenStreamComponents(source, new UrlTokenizingFilter(source));
            }
        };
    }

    /**
     * test some example domains
     */
    public void testExamples() throws IOException {
        String[] expected = new String[2];
        expected[0] = "domain";
        expected[1] = "test";
        assertAnalyzesTo(analyzer, "http://www.domain.com/test.php", expected);
        checkOneTerm(analyzer, "https://apache.org", "apache");
    }

    /**
     * copied from
     * http://svn.apache.org/repos/asf/lucene/dev/trunk/lucene/analysis/common/src/test/org/apache/lucene/analysis/en/TestEnglishMinimalStemFilter.java
     * blast some random strings through the analyzer
     */
    public void testRandomStrings() throws Exception {
        checkRandomData(random(), analyzer, 1000 * RANDOM_MULTIPLIER);
    }

    /**
     * copied from
     * http://svn.apache.org/repos/asf/lucene/dev/trunk/lucene/analysis/common/src/test/org/apache/lucene/analysis/en/TestEnglishMinimalStemFilter.java
     *
     * @throws IOException
     */
    public void testEmptyTerm() throws IOException {
        Analyzer a = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
                Tokenizer tokenizer = new KeywordTokenizer(reader);
                return new TokenStreamComponents(tokenizer, new UrlTokenizingFilter(tokenizer));
            }
        };
        checkOneTerm(a, "", "");
    }
}
