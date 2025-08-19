package com.yuri.love.views.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeDrawer(onCloseDrawer: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "菜单",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        NavigationDrawerItem(
            label = { Text("首页") },
            selected = false,
            onClick = {
                onCloseDrawer()
                // 处理导航
            },
        )

        NavigationDrawerItem(
            label = { Text("设置") },
            selected = false,
            onClick = {
                onCloseDrawer()
                // 处理导航
            },
        )

        NavigationDrawerItem(
            label = { Text("关于") },
            selected = false,
            onClick = {
                onCloseDrawer()
                // 处理导航
            },
        )
    }
}
