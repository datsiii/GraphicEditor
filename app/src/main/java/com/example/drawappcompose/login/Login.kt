package com.example.drawappcompose.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
//import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Scaffold
import com.example.drawappcompose.R
import com.example.drawappcompose.ui.theme.DrawAppComposeTheme
import com.example.drawappcompose.ui.theme.MGray
import com.example.drawappcompose.ui.theme.MPinkPurple
import com.example.drawappcompose.ui.theme.MYellow

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToHomePage: () -> Unit,
    onNavToSignUpPage: () -> Unit
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.loginError != null
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.log),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.size(400.dp))
            val fontFamily = FontFamily(
                Font(R.font.museomodernoblack, FontWeight.ExtraBold)
            )
            Text(
                text = "Sign In",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 40.sp,
                    color = MPinkPurple
                )
            )

            if (isError) {
                Text(
                    text = loginUiState?.loginError ?: "unknown error",
                    color = Color.Red,
                    fontFamily = fontFamily
                )
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = loginUiState?.userName ?: "",
                onValueChange = { loginViewModel?.onUserNameChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = "Email",
                        fontFamily = fontFamily,
                        color = MGray
                    )
                },
                isError = isError
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = loginUiState?.password ?: "",
                onValueChange = { loginViewModel?.onPasswordChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = "Password",
                        fontFamily = fontFamily,
                        color = MGray
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                isError = isError,
                //colors = Color(0xFF9E317E)
            )

            Button(
                onClick = { loginViewModel?.loginUser(context) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent // Устанавливаем цвет контейнера в прозрачный
                ),
                shape = RoundedCornerShape(10.dp), // Устанавливаем форму кнопки
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(MYellow, MPinkPurple)
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Sign In",
                    fontFamily = fontFamily
                )
            }
            Spacer(modifier = Modifier.size(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                /*Text(text = "Don't have an Account?",
                fontFamily = fontFamily,
                color = MGray)*/
                //Spacer(modifier = Modifier.size(8.dp))
                TextButton(
                    onClick = { onNavToSignUpPage.invoke() },
                    //modifier = Modifier.padding(vertical = 1.dp)
                ) {
                    Text(
                        text = "Don't have an Account? Sign Up",
                        fontFamily = fontFamily,
                        color = MGray
                    )
                }

            }
            if (loginUiState?.isLoading == true) {
                CircularProgressIndicator()
            }
            LaunchedEffect(key1 = loginViewModel?.hasUser) {
                if (loginViewModel?.hasUser == true) {
                    onNavToHomePage.invoke()
                }
            }

        }
    }
}

@Composable
fun SignUpScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToHomePage: () -> Unit,
    onNavToLoginPage: () -> Unit
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.signUpError != null
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.reg),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(400.dp))
            val fontFamily = FontFamily(
                Font(R.font.museomodernoblack, FontWeight.ExtraBold)
            )
            Text(
                text = "Sign Up",
                fontFamily = fontFamily,
                fontWeight = FontWeight.Black,
                color = MPinkPurple,
                fontSize = 40.sp
            )

            if (isError) {
                Text(
                    text = loginUiState?.signUpError ?: "unknown error",
                    color = Color.Red,
                    fontFamily = fontFamily
                )
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = loginUiState?.userNameSignUp ?: "",
                onValueChange = { loginViewModel?.onUserNameChangeSignUp(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Email",
                        fontFamily = fontFamily,
                        color = MGray)
                },
                isError = isError
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = loginUiState?.passwordSignUp ?: "",
                onValueChange = { loginViewModel?.onPasswordChangeSignUp(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Password",
                        fontFamily = fontFamily,
                        color = MGray)
                },
                visualTransformation = PasswordVisualTransformation(),
                isError = isError
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = loginUiState?.confirmPasswordSignUp ?: "",
                onValueChange = { loginViewModel?.onConfirmPasswordChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Confirm password",
                        fontFamily = fontFamily,
                        color = MGray)
                },
                visualTransformation = PasswordVisualTransformation(),
                isError = isError
            )

            Button(onClick = { loginViewModel?.createUser(context) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent // Устанавливаем цвет контейнера в прозрачный
                ),
                shape = RoundedCornerShape(10.dp), // Устанавливаем форму кнопки
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(MYellow, MPinkPurple)
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 3.dp)
            ) {
                Text(text = "Sign Up",
                    fontFamily = fontFamily)
            }
            Spacer(modifier = Modifier.size(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                //Text(text = "Already have an Account?")
                Spacer(modifier = Modifier.size(8.dp))
                TextButton(onClick = { onNavToLoginPage.invoke() }
                ) {
                    Text(text = "Already have an Account? Sign In",
                        fontFamily = fontFamily,
                        color = MGray)
                }

            }
            if (loginUiState?.isLoading == true) {
                CircularProgressIndicator()
            }
            LaunchedEffect(key1 = loginViewModel?.hasUser) {
                if (loginViewModel?.hasUser == true) {
                    onNavToHomePage.invoke()
                }
            }
            Image(
                painter = painterResource(id = R.drawable.reg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun PrevLoginScreen(){
    DrawAppComposeTheme {
        LoginScreen(onNavToHomePage = { /*TODO*/ }) {
            
        }

    }

}
@Preview(showSystemUi = true)
@Composable
fun PrevSignUpScreen(){
    DrawAppComposeTheme {
        SignUpScreen(onNavToHomePage = { /*TODO*/ }) {

        }

    }

}

