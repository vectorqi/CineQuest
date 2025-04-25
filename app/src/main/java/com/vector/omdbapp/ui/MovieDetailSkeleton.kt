package com.vector.omdbapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MovieDetailSkeleton() {
    val shimmerBrush = rememberShimmerBrush()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Poster skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(brush = shimmerBrush)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(28.dp)
                .padding(horizontal = 16.dp)
                .background(brush = shimmerBrush)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sections skeleton
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(20.dp)
                    .padding(horizontal = 16.dp)
                    .background(brush = shimmerBrush)
            )
            Spacer(modifier = Modifier.height(8.dp))
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(brush = shimmerBrush)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
