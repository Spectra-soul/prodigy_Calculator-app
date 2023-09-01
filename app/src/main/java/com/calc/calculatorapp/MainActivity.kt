package com.calc.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calc.calculatorapp.ui.theme.CalculatorAppTheme
import com.calc.calculatorapp.ui.theme.refresh_color

class MainActivity : ComponentActivity() {
    private val viewModel:CalcView by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    CalculatorContent(viewModel=viewModel)
                }
            }
        }
    }
}

@Composable
fun CalculatorContent(viewModel: CalcView) {
    val darkModeEnabled by LocalTheme.current.darkMode.collectAsState()
    val textColor = if (darkModeEnabled) Color.White else Color.Black
    val themeCalc = LocalTheme.current

    val calculatorButtons = remember {
        mutableStateListOf(
            CalculatorButton(icon = Icons.Outlined.Refresh, type = CalculatorButtonType.Reset),
            CalculatorButton("%", CalculatorButtonType.Action),
            CalculatorButton("⌫", type= CalculatorButtonType.Backspace),
            CalculatorButton("=", CalculatorButtonType.Action),

            CalculatorButton("7", CalculatorButtonType.Normal),
            CalculatorButton("8", CalculatorButtonType.Normal),
            CalculatorButton("9", CalculatorButtonType.Normal),
            CalculatorButton("÷", CalculatorButtonType.Action),

            CalculatorButton("4", CalculatorButtonType.Normal),
            CalculatorButton("5", CalculatorButtonType.Normal),
            CalculatorButton("6", CalculatorButtonType.Normal),
            CalculatorButton("x", CalculatorButtonType.Action),

            CalculatorButton("1", CalculatorButtonType.Normal),
            CalculatorButton("2", CalculatorButtonType.Normal),
            CalculatorButton("3", CalculatorButtonType.Normal),
            CalculatorButton("-", CalculatorButtonType.Action),

            CalculatorButton("00", CalculatorButtonType.Normal),
            CalculatorButton("0", CalculatorButtonType.Normal),
            CalculatorButton(".", CalculatorButtonType.Normal),
            CalculatorButton("+", CalculatorButtonType.Action)
        )
    }
    val (uiText,setUiText) = remember {
        mutableStateOf("0")
    }
    LaunchedEffect(uiText){
        if (uiText.startsWith("0") && uiText != "0"){
            setUiText(uiText.substring(1))
        }
    }
    val (input,setInput) = remember {
        mutableStateOf<String?>(null)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                text = uiText,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(32.dp))
            LazyVerticalGrid(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary).padding(8.dp),
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(calculatorButtons) {
                    CalcButton(
                        button = it,
                        textColor = textColor,
                        onClick = {
                            when(it.type){
                                CalculatorButtonType.Normal->{
                                    runCatching {
                                        setUiText(uiText.toInt().toString()+it.text)
                                    }.onFailure { throwable->
                                        setUiText(uiText+it.text)
                                    }
                                    setInput((input ?: "") + it.text)
                                    if (viewModel.action.value.isNotEmpty()){
                                        if (viewModel.secondNumber.value == null){
                                            viewModel.setSecondNumber(it.text!!.toDouble())
                                        }else{
                                            if (viewModel.secondNumber.value.toString().split(".")[1] == "0"){
                                                viewModel.setSecondNumber((viewModel.secondNumber.value.toString().split(".").first()+it.text!!).toDouble())
                                            }else{
                                                viewModel.setSecondNumber((viewModel.secondNumber.value.toString()+it.text!!).toDouble())
                                            }
                                        }
                                    }
                                }
                                CalculatorButtonType.Action->{
                                    if (it.text == "="){
                                        val result = viewModel.getResult()
                                        setUiText(result.toString())
                                        setInput(null)
                                        viewModel.resetAll()
                                    }else{
                                        runCatching {
                                            setUiText(uiText.toInt().toString()+it.text)
                                        }.onFailure { throwable->
                                            setUiText(uiText+it.text)
                                        }
                                        if (input != null){
                                            if (viewModel.firstNumber.value == null ){
                                                viewModel.setFirstNumber(input.toDouble())
                                            }else{
                                                viewModel.setSecondNumber(input.toDouble())
                                            }
                                            viewModel.setAction(it.text!!)
                                            setInput(null)
                                        }
                                    }
                                }

                                CalculatorButtonType.Backspace -> {
                                    if (uiText.length > 1) {
                                        setUiText(uiText.dropLast(1))
                                        setInput(input?.dropLast(1))
                                    } else {
                                        setUiText("0")
                                        setInput(null)
                                    }
                                }

                                CalculatorButtonType.Reset->{
                                    setUiText("0")
                                    setInput(null)
                                    viewModel.resetAll()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.clip(RoundedCornerShape(bottomEnd= 15.dp, bottomStart= 15.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .padding(horizontal = 20.dp, vertical = 5.dp)
        ) {
            Text(
                text = "Calculator",
                style = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 5.dp).clickable
                {
                    themeCalc.toggleTheme()
                },
                color = textColor
            )
        }
    }
}

@Composable
fun CalcButton(button: CalculatorButton, textColor:Color,onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .fillMaxHeight()
            .aspectRatio(1f)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        val contentColor =
            if (button.type == CalculatorButtonType.Reset)
                 refresh_color
            else
                textColor

        if (button.text != null) {
            Text(
                button.text,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = if (button.type == CalculatorButtonType.Action) 25.sp else 20.sp
            )
        } else {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = button.icon!!,
                contentDescription = null,
                tint = contentColor
            )
        }
    }
}

data class CalculatorButton(
    val text: String? = null,
    val type: CalculatorButtonType,
    val icon: ImageVector? = null,
)
enum class CalculatorButtonType {
    Normal, Action, Reset, Backspace
}

@Preview
@Composable
fun CalculatorContentPreview() {
    CalculatorContent(CalcView())
}