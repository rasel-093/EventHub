package com.example.eventshub.presentation.events
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eventshub.R
import com.example.eventshub.components.EventCard
import com.example.eventshub.presentation.events.createevent.CreateOrEditEventDialog
import com.example.eventshub.ui.theme.primaryColor
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder

@Composable
fun EventsScreen(
    innerPadding: PaddingValues,
    navController: NavHostController,
    viewModel: EventsViewModel = koinViewModel()
) {
    val state by viewModel.state
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateEventDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val eventsToShow = if (selectedTab == 0) state.upcomingEvents else state.pastEvents
    LaunchedEffect(navController.currentBackStackEntry) {
        // always refresh when returning to this screen
        viewModel.loadEvents()
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateEventDialog = true },
                containerColor = primaryColor,
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Event")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = Color.White,
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = { selectedTab = 0 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 0) primaryColor else Color.LightGray
                    )
                ) { Text("UPCOMING", color = Color.White) }

                Button(
                    onClick = { selectedTab = 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 1) primaryColor else Color.LightGray
                    )
                ) { Text("PAST EVENTS", color = Color.White) }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (eventsToShow.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(painter = painterResource(id = R.drawable.event_icon), contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No Events", fontSize = 20.sp)
                    Text("Create an event and make some memories.", fontSize = 14.sp, color = Color.Gray)
                }
            } else {
                LazyColumn {
                    itemsIndexed(eventsToShow) { _, event ->
                        EventCard(
                            event, onClick = {
                                val gson = Gson()
                                val encoded = URLEncoder.encode(gson.toJson(event), "UTF-8")
                                navController.navigate("eventdetails/$encoded")
                            }
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(viewModel.snackbarMessage) {
        viewModel.snackbarMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearSnackbarMessage()
            }
        }
    }
    if (showCreateEventDialog) {
        CreateOrEditEventDialog(
            onDismiss = { showCreateEventDialog = false },
            onConfirm = { event ->
                viewModel.createEvent(event)
                showCreateEventDialog = false
                viewModel.loadEvents()
            }
        )
    }
}


