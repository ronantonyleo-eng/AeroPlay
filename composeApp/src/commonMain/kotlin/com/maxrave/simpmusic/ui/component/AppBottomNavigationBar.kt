package com.maxrave.simpmusic.ui.component

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.maxrave.simpmusic.Platform
import com.maxrave.simpmusic.extension.greyScale
import com.maxrave.simpmusic.getPlatform
import com.maxrave.simpmusic.ui.navigation.destination.home.AnalyticsDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.HomeDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.LibraryDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.MixForYouDestination
import com.maxrave.simpmusic.ui.navigation.destination.search.SearchDestination
import com.maxrave.simpmusic.ui.theme.typo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import simpmusic.composeapp.generated.resources.*
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
fun AppBottomNavigationBar(
    startDestination: Any = HomeDestination,
    navController: NavController,
    showAnalyticsTab: Boolean = false,
    showMixForYouTab: Boolean = false,
    reloadDestinationIfNeeded: (KClass<*>) -> Unit = { _ -> },
) {
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

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .imePadding(),
    ) {
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
                    AeroFlatTabItem(
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
private fun AeroFlatTabItem(
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

@Composable
fun AppNavigationRail(
    startDestination: Any = HomeDestination,
    navController: NavController,
    showAnalyticsTab: Boolean = false,
    showMixForYouTab: Boolean = false,
    reloadDestinationIfNeeded: (KClass<*>) -> Unit = { _ -> },
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val railScreens = listOf(
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
    fun selectScreen(screen: BottomNavScreen) {
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

    if (getPlatform() == Platform.Desktop) {
        val sidebarShape = RoundedCornerShape(12.dp)
        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(240.dp)
                    .padding(8.dp)
                    .clip(sidebarShape)
                    .background(AeroBarBackground)
                    .border(1.dp, Color(0x33FFFFFF), sidebarShape)
                    .padding(horizontal = 12.dp, vertical = 18.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Brush.horizontalGradient(AeroRainbow)),
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Image(
                    painter = painterResource(Res.drawable.aero_final),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(CircleShape),
                )
                Text(
                    text = stringResource(Res.string.app_name),
                    style = typo().titleMedium,
                    color = AeroWhite,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.height(24.dp))
            railScreens.forEach { screen ->
                val selected = selectedIndex == screen.ordinal
                val itemColor = if (selected) AeroWhite else AeroSilver
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) AeroSelectedGlow else Color.Transparent)
                            .clickable { selectScreen(screen) }
                            .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    CompositionLocalProvider(LocalContentColor provides itemColor) {
                        screen.icon()
                    }
                    Text(
                        text = stringResource(screen.title),
                        style = typo().bodyMedium.copy(
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        ),
                        color = itemColor,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    } else {
        NavigationRail {
            Spacer(Modifier.height(16.dp))
            Box(Modifier.padding(horizontal = 16.dp)) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.aero_final),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .height(32.dp)
                                .clip(CircleShape),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            railScreens.forEach { screen ->
                NavigationRailItem(
                    icon = screen.icon,
                    label = {
                        Text(
                            stringResource(screen.title),
                            style =
                                if (selectedIndex == screen.ordinal) {
                                    typo().bodySmall
                                } else {
                                    typo().bodySmall.greyScale()
                                },
                        )
                    },
                    selected = selectedIndex == screen.ordinal,
                    onClick = { selectScreen(screen) },
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}