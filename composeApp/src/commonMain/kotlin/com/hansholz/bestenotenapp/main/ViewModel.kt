package com.hansholz.bestenotenapp.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.window.core.layout.WindowWidthSizeClass
import com.hansholz.bestenotenapp.api.*
import dev.chrisbanes.haze.HazeState

class ViewModel : ViewModel() {
    val api = BesteSchuleApi(AuthToken.TOKEN)

    val hazeBackgroundState = HazeState()
    val hazeBackgroundState2 = HazeState()
    val hazeBackgroundState3 = HazeState()

    val currentScreen = mutableStateOf(Screen.HOME)
    val compactDrawerState = mutableStateOf(DrawerState(DrawerValue.Closed))
    val mediumExpandedDrawerState = mutableStateOf(DrawerState(DrawerValue.Open))

    val finalGrades = mutableStateListOf<FinalGrade>()
    val subjects = mutableStateListOf<Subject>()

    val collections = mutableStateListOf<GradeCollection>()
    val years = mutableStateListOf<Year>()

    suspend fun closeOrOpenDrawer(windowWidthSizeClass: WindowWidthSizeClass) {
        if (windowWidthSizeClass == WindowWidthSizeClass.Companion.COMPACT) {
            if (compactDrawerState.value.isClosed) {
                compactDrawerState.value.open()
            } else {
                compactDrawerState.value.close()
            }
        } else {
            if (mediumExpandedDrawerState.value.isClosed) {
                mediumExpandedDrawerState.value.open()
            } else {
                mediumExpandedDrawerState.value.close()
            }
        }
    }

    suspend fun getCollections(): List<GradeCollection> {
        collections.clear()
        val collection = api.getCollections(include = listOf("grades", "interval", "grades.histories"))
        collections.addAll(collection.data)
        if (collection.meta?.lastPage!! > 1) {
            for (i in 2..collection.meta.lastPage) {
                collections.addAll(api.getCollections(include = listOf("grades", "interval", "grades.histories"), page = i).data)
            }
        }
        return collections
    }
}