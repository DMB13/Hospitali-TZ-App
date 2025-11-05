package com.hospital.tz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hospital.tz.data.Hospital
import com.hospital.tz.data.HospitalType
import com.hospital.tz.ui.viewmodels.MapViewModel
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MapScreen() {
    val viewModel: MapViewModel = viewModel()
    val hospitals by viewModel.hospitals.collectAsState()
    val selectedHospital by viewModel.selectedHospital.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val isOfflineMode by viewModel.isOfflineMode.collectAsState()

    var showHospitalList by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Map Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Medical Centers Map",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(onClick = { viewModel.toggleOfflineMode() }) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "Toggle Offline Mode",
                        tint = if (isOfflineMode) Color.Green else Color.Gray
                    )
                }

                IconButton(onClick = { showHospitalList = !showHospitalList }) {
                    Icon(Icons.Default.Search, contentDescription = "Search Hospitals")
                }
            }
        }

        // Search Bar (when list is shown)
        if (showHospitalList) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search hospitals by region") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        // Main Content
        if (showHospitalList) {
            // Hospital List View
            val filteredHospitals = if (searchQuery.isBlank()) {
                hospitals
            } else {
                hospitals.filter { it.region.contains(searchQuery, ignoreCase = true) }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Medical Centers (${filteredHospitals.size})",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(filteredHospitals) { hospital ->
                    HospitalListItem(hospital) {
                        viewModel.selectHospital(hospital)
                        showHospitalList = false
                    }
                }
            }
        } else {
            // Map View
            Box(modifier = Modifier.fillMaxSize()) {
                // Placeholder for Mapbox MapView
                // In a real implementation, this would be replaced with actual Mapbox integration
                MapPlaceholder(hospitals, selectedHospital, userLocation)

                // Hospital Details Card (if selected)
                selectedHospital?.let { hospital ->
                    HospitalDetailsCard(
                        hospital = hospital,
                        onDismiss = { viewModel.clearSelection() },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MapPlaceholder(
    hospitals: List<Hospital>,
    selectedHospital: Hospital?,
    userLocation: Pair<Double, Double>?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E8)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tanzania Medical Centers Map",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${hospitals.size} medical centers loaded",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hospital type summary
            val hospitalsCount = hospitals.count { it.type == HospitalType.HOSPITAL }
            val dispensariesCount = hospitals.count { it.type == HospitalType.DISPENSARY }
            val pharmaciesCount = hospitals.count { it.type == HospitalType.PHARMACY }

            Column(horizontalAlignment = Alignment.Start) {
                Text("🏥 Hospitals: $hospitalsCount", style = MaterialTheme.typography.body2)
                Text("🏥 Dispensaries: $dispensariesCount", style = MaterialTheme.typography.body2)
                Text("💊 Pharmacies: $pharmaciesCount", style = MaterialTheme.typography.body2)
            }

            selectedHospital?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Selected: ${it.name}",
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
        }
    }
}

@Composable
fun HospitalListItem(hospital: Hospital, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hospital.name,
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${hospital.type.name} • ${hospital.region}",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }

                Text(
                    text = getHospitalTypeIcon(hospital.type),
                    style = MaterialTheme.typography.h6
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = hospital.address,
                style = MaterialTheme.typography.body2
            )

            hospital.phone?.let {
                Text(
                    text = "📞 $it",
                    style = MaterialTheme.typography.caption,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Services: ${hospital.services}",
                style = MaterialTheme.typography.caption,
                maxLines = 2
            )
        }
    }
}

@Composable
fun HospitalDetailsCard(hospital: Hospital, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 8.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = hospital.name,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onDismiss) {
                    Text("✕", style = MaterialTheme.typography.h6)
                }
            }

            Text(
                text = "${hospital.type.name} • ${hospital.region}, ${hospital.district}",
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = hospital.address,
                style = MaterialTheme.typography.body2
            )

            hospital.phone?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Phone: $it",
                    style = MaterialTheme.typography.body2
                )
            }

            hospital.email?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Email: $it",
                    style = MaterialTheme.typography.body2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Available Services:",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = hospital.services,
                style = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Navigate to hospital */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Navigate")
                }

                OutlinedButton(
                    onClick = { /* Call hospital */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Call")
                }
            }
        }
    }
}

private fun getHospitalTypeIcon(type: HospitalType): String {
    return when (type) {
        HospitalType.HOSPITAL -> "🏥"
        HospitalType.DISPENSARY -> "🏥"
        HospitalType.PHARMACY -> "💊"
    }
}
