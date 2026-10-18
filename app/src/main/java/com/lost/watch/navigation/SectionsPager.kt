package com.lost.watch.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.molecules.PageIndicator
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R
import com.lost.watch.feature.contacts.ContactsScreen
import com.lost.watch.feature.destinations.DestinationsScreen
import com.lost.watch.feature.home.HomeScreen
import com.lost.watch.feature.routines.RoutinesScreen
import com.lost.watch.feature.settings.SettingsScreen

/**
 * Navegación global: las cinco secciones en un pager horizontal. Dentro de cada sección
 * la corona desplaza la lista; el indicador de abajo dice en qué sección está el usuario.
 */
@Composable
fun SectionsPager(navigator: LostNavigator) {
    val sections = GlobalSection.entries
    val pagerState = rememberPagerState { sections.size }
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            SectionPage(sections[page], navigator)
        }
        val current = sections[pagerState.currentPage]
        PageIndicator(
            pageCount = sections.size,
            selectedPage = pagerState.currentPage,
            description = stringResource(
                R.string.cd_section_position,
                stringResource(current.label),
                pagerState.currentPage + 1,
                sections.size
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = SemanticTokens.Spacing.sm)
        )
    }
}

@Composable
private fun SectionPage(section: GlobalSection, navigator: LostNavigator) {
    when (section) {
        GlobalSection.HOME -> HomeScreen(
            onOpenMonitoring = { navigator.open(LostDestination.Monitoring) },
            onOpenDemo = { navigator.open(LostDestination.Demo) }
        )

        GlobalSection.ROUTINES -> RoutinesScreen(
            onOpenRoutine = navigator::openRoutine,
            onOpenLearning = navigator::openLearning,
            onOpenWeek = { navigator.open(LostDestination.Insights) }
        )

        GlobalSection.DESTINATIONS -> DestinationsScreen(onOpenDestination = navigator::openNewPlace)

        GlobalSection.CONTACTS -> ContactsScreen()

        GlobalSection.SETTINGS -> SettingsScreen(onOpen = navigator::open)
    }
}
