package com.maxrave.simpmusic.ui.component

import androidx.compose.material3.LocalContentColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.maxrave.domain.data.player.GenericMediaItem
import com.maxrave.simpmusic.expect.ui.PlatformBackdrop
import com.maxrave.simpmusic.ui.navigation.destination.home.AnalyticsDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.HomeDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.LibraryDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.MixForYouDestination
import com.maxrave.simpmusic.ui.navigation.destination.search.SearchDestination
import com.maxrave.simpmusic.ui.screen.MiniPlayer
import com.maxrave.simpmusic.ui.theme.typo
import com.maxrave.simpmusic.viewModel.SharedViewModel
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KClass

private val AeroBarBackground = Color(0xE60A0A0A)
private val AeroRainbow = listOf(
    Color(0xFFFF3B3B),
    Color(0xFFFF9A3B),
    Color(0xFFFFE23B),
    Color(0xFF52E37A),
    Color(0xFF3BC8FF),
    Color(0xFF6A6AFF),
    Color(0xFFB44BFF),
    Color(0xFFFF3B3B),
)
private val AeroSilver = Color(0xFFB8B8B8)
private val AeroWhite = Color(0xFFFFFFFF)
private val AeroSelectedGlow = Color(0x33FFFFFF)
private val AeroSelectedBorder = Color(0x66FFFFFF)
private val AeroTabTopShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

@Composable
actual fun LiquidGlassAppBottomNavigationBar(
    startDestination: Any,
    navController: NavController,
    backdrop: PlatformBackdrop,
    viewModel: SharedViewModel,
    isScrolledToTop: Boolean,
    showAnalyticsTab: Boolean,
    showMixForYouTab: Boolean,
    onOpenNowPlaying: () -> Unit,
    reloadDestinationIfNeeded: (KClass<*>) -> Unit,
) {
    val nowPlayingData by viewModel.nowPlayingState.collectAsStateWithLifecycle()
    val isShowMiniPlayer by remember {
        derivedStateOf {
            val item = nowPlayingData?.mediaItem
            item != null && item != GenericMediaItem.EMPTY
        }
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val barTabs = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Search,
        BottomNavScreen.Library,
    )

    var selectedIndex by rememberSaveable {
        mutableIntStateOf(
            when (startDestination) {
                is HomeDestination -> BottomNavScreen.Home.ordinal
                is SearchDestination -> BottomNavScreen.Search.ordinal
                is LibraryDestination -> BottomNavScreen.Library.ordinal
                is AnalyticsDestination -> BottomNavScreen.Home.ordinal
                is MixForYouDestination -> BottomNavScreen.Home.ordinal
                else -> BottomNavScreen.Home.ordinal
            },
        )
    }

    fun selectTab(screen: BottomNavScreen) {
        if (selectedIndex == screen.ordinal) {
            if (currentBackStackEntry?.destination?.hierarchy?.any {
                    it.hasRoute(screen.destination::class)
                } == true
            ) {
                reloadDestinationIfNeeded(screen.destination::class)
            } else {
                navController.navigate(screen.destination)
            }
        } else {
            selectedIndex = screen.ordinal
            navController.navigate(screen.destination) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .imePadding(),
    ) {
        if (isShowMiniPlayer) {
            MiniPlayer(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .height(56.dp),
                backdrop = backdrop,
                onClick = onOpenNowPlaying,
                onClose = {
                    viewModel.stopPlayer()
                    viewModel.isServiceRunning = false
                },
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(AeroTabTopShape)
                    .background(AeroBarBackground)
                    .border(1.dp, Color(0x33FFFFFF), AeroTabTopShape),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Brush.horizontalGradient(AeroRainbow))
                        .align(Alignment.TopCenter),
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                barTabs.forEach { screen ->
                    val selected = selectedIndex == screen.ordinal
                    AeroTabItem(
                        screen = screen,
                        selected = selected,
                        modifier = Modifier.weight(1f),
                        onClick = { selectTab(screen) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AeroTabItem(
    screen: BottomNavScreen,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val contentColor = if (selected) AeroWhite else AeroSilver
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (selected) AeroSelectedGlow else Color.Transparent)
                    .then(
                        if (selected) {
                            Modifier.border(1.dp, AeroSelectedBorder, CircleShape)
                        } else {
                            Modifier
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                screen.icon()
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = stringResource(screen.title),
            style = typo().bodySmall.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = contentColor,
            maxLines = 1,
        )
    }
}