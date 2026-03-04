package com.mariya.skillcraftcalculator.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mariya.skillcraftcalculator.domain.CalculatorEngine.Action
import com.mariya.skillcraftcalculator.domain.CalculatorEngine.Op
import com.mariya.skillcraftcalculator.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(vm: CalculatorViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    val keys = listOf(
        listOf(Key("C", Kind.UTIL) { Action.Clear }, Key("⌫", Kind.UTIL) { Action.Backspace }, Key("%", Kind.UTIL) { Action.Percent }, Key("÷", Kind.OP) { Action.Operator(Op.DIV) }),
        listOf(Key("7", Kind.NUM) { Action.Digit('7') }, Key("8", Kind.NUM) { Action.Digit('8') }, Key("9", Kind.NUM) { Action.Digit('9') }, Key("×", Kind.OP) { Action.Operator(Op.MUL) }),
        listOf(Key("4", Kind.NUM) { Action.Digit('4') }, Key("5", Kind.NUM) { Action.Digit('5') }, Key("6", Kind.NUM) { Action.Digit('6') }, Key("-", Kind.OP) { Action.Operator(Op.SUB) }),
        listOf(Key("1", Kind.NUM) { Action.Digit('1') }, Key("2", Kind.NUM) { Action.Digit('2') }, Key("3", Kind.NUM) { Action.Digit('3') }, Key("+", Kind.OP) { Action.Operator(Op.ADD) }),
        listOf(Key("±", Kind.UTIL) { Action.ToggleSign }, Key("0", Kind.NUM) { Action.Digit('0') }, Key(".", Kind.NUM) { Action.Dot }, Key("=", Kind.EQ) { Action.Equals })
    )

    Scaffold(containerColor = Color(0xFF0B1020)) { pad ->
        Column(
            modifier = Modifier.padding(pad).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(Color(0xFF111827), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text = state.expression,
                    fontSize = 16.sp,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.align(Alignment.TopEnd),
                    maxLines = 1
                )

                val mainValue =
                    if (state.liveResult.isNotBlank() && state.expression.isNotBlank())
                        state.liveResult
                    else
                        state.display

                Text(
                    text = mainValue,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.End,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                keys.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { key ->
                            CalcButton(
                                label = key.label,
                                kind = key.kind,
                                modifier = Modifier.weight(1f)
                            ) { vm.onAction(key.action()) }
                        }
                    }
                }
            }
        }
    }
}

private enum class Kind { NUM, OP, UTIL, EQ }
private data class Key(val label: String, val kind: Kind, val action: () -> Action)

@Composable
private fun CalcButton(label: String, kind: Kind, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val bg = when (kind) {
        Kind.NUM -> Color(0xFF1F2937)
        Kind.OP -> Color(0xFF2563EB)
        Kind.UTIL -> Color(0xFF374151)
        Kind.EQ -> Color(0xFF22C55E)
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bg)
    ) {
        Text(label, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = Color.White)
    }
}