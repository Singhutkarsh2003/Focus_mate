package com.example.focusmate.ui.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.focusmate.R

@Composable
fun SubCard(
    modifier: Modifier = Modifier,
    subName: String,
    gradientColor: List<Color>,
    onClick : () -> Unit
){
    Box (
        modifier = modifier.size(170.dp)
            .clickable{ onClick() }
            .background(
                brush = Brush.verticalGradient(gradientColor),
                shape = MaterialTheme.shapes.medium
            )
    ){
        Column (
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.Center
        ){
            Image(
                painter = painterResource(R.drawable.img2_book),
                contentDescription = subName,
                modifier = Modifier.size(90.dp)
            )
            Text(
                text = subName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                maxLines = 1
            )
        }
    }

}