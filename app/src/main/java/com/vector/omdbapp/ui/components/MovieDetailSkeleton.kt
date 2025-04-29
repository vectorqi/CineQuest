package com.vector.omdbapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vector.omdbapp.ui.theme.rememberShimmerBrush

@Composable
fun MovieDetailSkeleton() {
    val shimmerBrush = rememberShimmerBrush()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top AppBar Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .background(brush = shimmerBrush)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Poster Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(brush = shimmerBrush)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(28.dp)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .background(brush = shimmerBrush)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sections Skeleton
        repeat(2) {
            // Section Header
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(20.dp)
                    .padding(horizontal = 16.dp)
                    .background(brush = shimmerBrush)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Section Items
            repeat(5) {
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