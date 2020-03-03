package com.example.search.repository.dispatcher_context

import kotlinx.coroutines.newFixedThreadPoolContext

val workersContext = newFixedThreadPoolContext(2, "workers")