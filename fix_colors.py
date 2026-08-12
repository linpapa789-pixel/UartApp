import os

file_path = "app/src/main/java/com/example/ui/screens/SettingsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacements = {
    "Color(0xFF10B981)": "SuccessGreen",
    "Color(0xFF06B6D4)": "AccentCyan",
    "Color(0xFFF59E0B)": "WarningYellow",
    "Color(0xFF94A3B8)": "TextSecondary",
    "Color(0xFF64748B)": "TextMutedColor",
    "Color(0xFFE3E2E6)": "TextPrimary",
    "Color(0x1AFFFFFF)": "CardBorder",
    "Color(0xFF1B1B1F)": "CardBackground",
    "Color(0xFF2E2E32)": "CardBackground",
    "Color.White": "TextPrimary",
    "Color.Black": "AppBackground"
}

for old, new in replacements.items():
    content = content.replace(old, new)

with open(file_path, "w") as f:
    f.write(content)
