package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.theme.ServixBlueDark
import com.example.core.theme.ServixBlueLight
import com.example.core.theme.ServixBluePrimary
import com.example.core.theme.ServixBlueSecondary
import com.example.core.theme.ServixBlueVeryLight
import com.example.domain.model.UserRole
import com.example.presentation.components.ServixOfficialBadgeCard
import com.example.presentation.components.ServixTagline
import com.example.presentation.viewmodel.AuthUiState
import com.example.presentation.viewmodel.MainViewModel

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    onAuthSuccess: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(UserRole.CLIENTE) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetMessage by remember { mutableStateOf<String?>(null) }
    var showDemoShortcuts by remember { mutableStateOf(false) }

    val authUiState by viewModel.authUiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Limpiar errores al cambiar de modo o rol
    LaunchedEffect(isRegisterMode, selectedRole) {
        localError = null
        viewModel.clearAuthError()
    }

    val isLoading = authUiState is AuthUiState.Loading

    // Fondo ambiental sutil con gradiente suave
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val bgGradient = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF061022),
                Color(0xFF0B1933),
                Color(0xFF061022)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF4F8FD),
                Color(0xFFFFFFFF),
                Color(0xFFEEF5FD)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("auth_screen")
            .background(bgGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Insignia Oficial 3D del Logotipo de ServiX
            ServixOfficialBadgeCard(
                size = 104.dp,
                modifier = Modifier
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), clip = false)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tipografía de Marca
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Servi",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "X",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Lema Oficial Corporativo
            ServixTagline(fontSize = 10.5.sp)

            Spacer(modifier = Modifier.height(20.dp))

            // Selector Moderno de Modo: Iniciar Sesión vs Registro
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val loginColor by animateColorAsState(
                        targetValue = if (!isRegisterMode) MaterialTheme.colorScheme.surface else Color.Transparent,
                        animationSpec = tween(200),
                        label = "loginTabColor"
                    )
                    val loginTextColor by animateColorAsState(
                        targetValue = if (!isRegisterMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(200),
                        label = "loginTextColor"
                    )

                    Surface(
                        onClick = { isRegisterMode = false },
                        shape = RoundedCornerShape(12.dp),
                        color = loginColor,
                        shadowElevation = if (!isRegisterMode) 2.dp else 0.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Iniciar Sesión",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (!isRegisterMode) FontWeight.Bold else FontWeight.Medium,
                                    color = loginTextColor
                                )
                            )
                        }
                    }

                    val registerColor by animateColorAsState(
                        targetValue = if (isRegisterMode) MaterialTheme.colorScheme.surface else Color.Transparent,
                        animationSpec = tween(200),
                        label = "regTabColor"
                    )
                    val registerTextColor by animateColorAsState(
                        targetValue = if (isRegisterMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(200),
                        label = "regTextColor"
                    )

                    Surface(
                        onClick = { isRegisterMode = true },
                        shape = RoundedCornerShape(12.dp),
                        color = registerColor,
                        shadowElevation = if (isRegisterMode) 2.dp else 0.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Registrarse",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isRegisterMode) FontWeight.Bold else FontWeight.Medium,
                                    color = registerTextColor
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selector de Experiencia (Cliente vs Administrador) con TestTags Preservados
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isClienteSelected = selectedRole == UserRole.CLIENTE
                    Button(
                        onClick = { selectedRole = UserRole.CLIENTE },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isClienteSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (isClienteSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        elevation = if (isClienteSelected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("auth_role_cliente")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Soy Cliente", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    val isAdminSelected = selectedRole == UserRole.ADMINISTRADOR
                    Button(
                        onClick = { selectedRole = UserRole.ADMINISTRADOR },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAdminSelected) ServixBlueDark else Color.Transparent,
                            contentColor = if (isAdminSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        elevation = if (isAdminSelected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("auth_role_admin")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Administrador", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Atajos de prueba: solo en builds debug, nunca en release.
            if (com.example.BuildConfig.DEBUG) Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                border = BorderStroke(0.8.dp, ServixBlueLight.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDemoShortcuts = !showDemoShortcuts },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Acceso Rápido de Prueba (Demo)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Icon(
                            imageVector = if (showDemoShortcuts) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    AnimatedVisibility(visible = showDemoShortcuts) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = "Selecciona un perfil para rellenar las credenciales al instante:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        isRegisterMode = false
                                        selectedRole = UserRole.CLIENTE
                                        email = "cliente.demo@servix.com"
                                        password = ""
                                        localError = null
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cliente Demo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        isRegisterMode = false
                                        selectedRole = UserRole.ADMINISTRADOR
                                        email = "admin@servix.com"
                                        password = ""
                                        localError = null
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Admin Demo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tarjeta Principal del Formulario
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRegisterMode) {
                            if (selectedRole == UserRole.CLIENTE) "Registro de Cliente" else "Registro de Administrador"
                        } else {
                            if (selectedRole == UserRole.CLIENTE) "Acceso de Cliente" else "Acceso Administrativo"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isRegisterMode) {
                            if (selectedRole == UserRole.CLIENTE) "Crea tu cuenta para cotizar, solicitar y dar seguimiento a servicios técnicos" else "Registra tu cuenta de administración y gestión operativa"
                        } else {
                            if (selectedRole == UserRole.CLIENTE) "Ingresa con tu correo para consultar tus órdenes y hablar con el soporte" else "Ingresa con tus credenciales operativas de ServiX"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    // Mensaje de Error
                    val errorMessage = localError ?: (authUiState as? AuthUiState.Error)?.message
                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Campos del Formulario
                    if (isRegisterMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre Completo *") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_name_input")
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Teléfono / WhatsApp (Opcional)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_phone_input")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico *") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (email.contains("@") && email.contains(".")) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Correo válido",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña (mín. 6 caracteres) *") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Alternar contraseña"
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (isRegisterMode) ImeAction.Next else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input")
                    )

                    if (isRegisterMode) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirmar Contraseña *") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LockReset,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_confirm_password_input")
                        )
                    }

                    if (!isRegisterMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    resetEmail = email
                                    resetMessage = null
                                    showResetDialog = true
                                }
                            ) {
                                Text(
                                    "¿Olvidaste tu contraseña?",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Botón Principal de Acción con Gradiente Oficial
                    Button(
                        onClick = {
                            localError = null
                            focusManager.clearFocus()
                            if (isRegisterMode) {
                                if (name.isBlank()) {
                                    localError = "Por favor ingresa tu nombre completo"
                                    return@Button
                                }
                                if (email.isBlank() || !email.contains("@")) {
                                    localError = "Ingresa un correo electrónico válido"
                                    return@Button
                                }
                                if (password.length < 6) {
                                    localError = "La contraseña debe tener al menos 6 caracteres"
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    localError = "Las contraseñas no coinciden"
                                    return@Button
                                }
                                viewModel.signUp(
                                    name = name,
                                    email = email,
                                    pass = password,
                                    phone = phone,
                                    role = selectedRole
                                ) {
                                    onAuthSuccess()
                                }
                            } else {
                                if (email.isBlank()) {
                                    localError = "Por favor ingresa tu correo"
                                    return@Button
                                }
                                if (password.isBlank()) {
                                    localError = "Por favor ingresa tu contraseña"
                                    return@Button
                                }
                                viewModel.signIn(
                                    email = email,
                                    pass = password,
                                    requestedRole = selectedRole
                                ) {
                                    onAuthSuccess()
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_submit_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isRegisterMode) "Crear Cuenta de ${selectedRole.displayName}" else "Ingresar como ${selectedRole.displayName}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.3.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Alternar Modo Registro / Login con TestTag Preservado
                    TextButton(
                        onClick = {
                            isRegisterMode = !isRegisterMode
                        },
                        modifier = Modifier.testTag("auth_toggle_mode_button")
                    ) {
                        Text(
                            text = if (isRegisterMode) "¿Ya tienes cuenta? Inicia sesión aquí" else "¿No tienes cuenta? Regístrate gratis aquí",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        // Diálogo de Recuperación de Contraseña
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.LockReset,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        "Recuperar Contraseña",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Column {
                        Text(
                            "Ingresa el correo asociado a tu cuenta para enviarte un enlace de restablecimiento seguro:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Correo Electrónico") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (resetMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = resetMessage!!,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.sendPasswordReset(resetEmail) { _, msg ->
                                resetMessage = msg
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Enviar Correo")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}
