// Copyright (c) 2026 Sabareesh M (saba1207B). All Rights Reserved.
// All rights reserved. No public license is granted.

package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FaqDataset
import com.example.nlp.TextPreprocessor
import com.example.nlp.TfidfEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CodeAlpha FAQ Bot", appName)
  }

  @Test
  fun `nlp preprocessor tokenizes and stems correctly`() {
    val tokens = TextPreprocessor.preprocess("How do I reset my account passwords and access API keys?")
    assertTrue(tokens.contains("reset"))
    assertTrue(tokens.contains("password"))
    assertTrue(tokens.contains("api"))
    assertTrue(tokens.contains("key"))
  }

  @Test
  fun `tfidf engine finds most similar FAQ`() {
    val preprocessedCorpus = FaqDataset.ALL_FAQS.map { faq ->
      val docText = "${faq.question} ${faq.tags.joinToString(" ")}"
      val tokens = TextPreprocessor.preprocess(docText)
      TextPreprocessor.extractNgrams(tokens)
    }
    val engine = TfidfEngine(preprocessedCorpus)
    val queryTokens = TextPreprocessor.extractNgrams(TextPreprocessor.preprocess("forgot my password reset link"))
    val results = engine.computeSimilarities(queryTokens)

    assertTrue(results.isNotEmpty())
    val topMatch = FaqDataset.ALL_FAQS[results.first().first]
    assertEquals(4, topMatch.id) // FAQ #4 is "How do I reset my account password?"
    assertTrue(results.first().second > 0.30f)
  }
}

