package com.example.testapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.testapp.pages.CropImagePage
import com.example.testapp.pages.GlideNativeImagePage
import com.example.testapp.pages.NavCropImage
import com.example.testapp.pages.NavGlideNativeImage
import com.example.testapp.ui.theme.TestappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestappTheme {
                TestappApp()
            }
        }
    }
}

//@PreviewScreenSizes
@Composable
fun TestappApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    val backStack = rememberNavBackStack(Home)

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = {
                        currentDestination = it
                        backStack.add(it.navKey)
                    }
                )
            }
        },
    ) {
        NavDisplay(
            modifier = Modifier.safeContentPadding(),
            // 导航堆栈
            backStack = backStack,
            // 导航条目装饰器（用于处理viewmodel和状态保存）
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it / 2 })
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it / 2 }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it / 2 }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            entryProvider = entryProvider {
                entry<Home> { Index1Page(backStack) }
                entry<Product> { Index2Page() }
                entry<About> { Index3Page() }
                entry<NavGlideNativeImage> { GlideNativeImagePage() }
                entry<NavCropImage> { CropImagePage() }
            }
        )
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
    val navKey: NavKey
) {
    HOME("Home", R.drawable.ic_home, Home),
    FAVORITES("Favorites", R.drawable.ic_favorite, Product("1")),
    PROFILE("Profile", R.drawable.ic_account_box, About),
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TestPagePreview() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { }
                )
            }
        },
    ) {
        Index1PagePreview()
    }

}