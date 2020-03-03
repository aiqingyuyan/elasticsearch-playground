package com.example.search.configuration

import kotlinx.coroutines.newFixedThreadPoolContext
import org.springframework.context.annotation.Configuration

@Configuration
class DispatcherContext {
  var workersContext = newFixedThreadPoolContext(2, "workers")
}
