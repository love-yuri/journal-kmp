package com.yuri.love.views.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuri.love.styles.GlobalFonts
import journal.composeapp.generated.resources.Res
import journal.composeapp.generated.resources.date
import journal.composeapp.generated.resources.weather_sun
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 日记卡片绘制
 */
@Composable
@Preview
fun JournalCardComposable() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color(0xFFFFB3D1),
                shape = RoundedCornerShape(10.dp))
            ,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(modifier = Modifier.padding(all = 12.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart).padding(bottom = 5.dp),
                    text = "美好的一天",
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    fontFamily = GlobalFonts.MapleMonoFont
                )
                Canvas(modifier = Modifier.size(12.dp).align(Alignment.CenterEnd)) {
                    drawCircle(
                        color = Color(0xFFF472B6),
                        style = Fill
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painterResource(Res.drawable.date),
                    null,
                    modifier = Modifier.size(18.dp),
                )
                Text("2024年8月14日", modifier = Modifier.padding(start = 8.dp, end = 12.dp), fontFamily = GlobalFonts.MapleMonoFont)
                Image(
                    painterResource(Res.drawable.weather_sun),
                    null,
                    modifier = Modifier.size(18.dp),
                )
                Text("晴天", modifier = Modifier.padding(start = 8.dp), fontFamily = GlobalFonts.MapleMonoFont)
            }
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier.padding(top = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically  // 垂直居中
            ) {
                // 标签 1
                Text(
                    text = "#生活",
                    style = TextStyle(
                        color = Color(0xFF7C3AED),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .background(Color(0xFFFEC7D7), shape = CircleShape)
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                )

                // 标签 2
                Text(
                    text = "#心情",
                    style = TextStyle(
                        color = Color(0xFFDB2777),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .background(Color(0xFFFEC7D7), shape = CircleShape)
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                )
            }
        }
    }
}
