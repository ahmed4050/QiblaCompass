# 📱 بوصلة القبلة - Android

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge)](https://developer.android.com/jetpack/compose)

## 📋 نظرة عامة

تطبيق أندرويد أصلي (Native) لتحديد اتجاه **القبلة** باستخدام مستشعرات الجهاز (مسرع + مغناطيس) و GPS. مبني بأحدث ممارسات تطوير أندرويد.

## ⭐ المميزات

### 🧭 البوصلة
- حساب دقيق باستخدام **Great Circle Formula**
- عرض اتجاه القبلة بالنسبة للشمال
- مسافة إلى الكعبة بالكيلومتر

### 📊 المستشعرات
- مسرع ثلاثي المحاور
- مغناطيس ثلاثي المحاور
- GPS و Network Location

### 🎨 التصميم
- **Material 3 Design**
- الوضع الداكن والفاتح
- تصميم عصري وأنيق

### 🏗️ البنية

```
Clean Architecture + MVVM
├── Domain Layer
│   ├── Entities
│   └── Use Cases
├── Data Layer
│   ├── Repository
│   └── Data Sources
├── Presentation Layer
│   ├── ViewModels
│   └── Composables
└── DI (Hilt)
```

## 🛠️ التقنيات

| التقنية | الاستخدام |
|---------|-----------|
| Kotlin | اللغة الأساسية |
| Jetpack Compose | واجهة المستخدم |
| Hilt | Dependency Injection |
| Coroutines + Flow | العمليات غير المتزامنة |
| Material 3 | التصميم |

## 📦 المتطلبات

- Android SDK 34
- JDK 17
- Android Studio Hedgehog+

## 🚀 التشغيل

```bash
# استنساخ المستودع
git clone https://github.com/ahmed4050/QiblaCompass.git

# فتح في Android Studio
# بناء المشروع
# تشغيل على جهاز أو محاكي
```

## 📱 الواجهة

```
┌────────────────────────────┐
│  🧭 بوصلة القبلة            │
├────────────────────────────┤
│                            │
│            N               │
│            ↑               │
│       W ──●── E            │
│            │               │
│            ↓               │
│            S               │
│                            │
│      ▲                    │
│      │ ← سهم القبلة       │
│                            │
├────────────────────────────┤
│  اتجاه القبلة: 127°       │
│  المسافة: 1,234 km        │
│  الموقع: مسقط، عمان       │
└────────────────────────────┘
```

## 🧪 الاختبارات

```bash
# تشغيل اختبارات الوحدة
./gradlew test

# تشغيل اختبارات UI
./gradlew connectedAndroidTest
```

## 📁 هيكل المشروع

```
QiblaCompass/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/ahmed4050/qiblacompass/
│   │   │   │       ├── di/           # Dependency Injection
│   │   │   │       ├── domain/       # Domain Layer
│   │   │   │       ├── data/         # Data Layer
│   │   │   │       └── presentation/ # UI Layer
│   │   │   └── res/
│   │   ├── test/                    # Unit Tests
│   │   └── androidTest/             # UI Tests
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

## 📚 المراجع

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Clean Architecture](https://developer.android.com/jetpack/arch/overview)
- [Qibla Calculation](https://en.wikipedia.org/wiki/Qibla)

## 👨‍💻 المؤلف

**Ahmed Al-Qassabi** - [GitHub](https://github.com/ahmed4050)

## 📄 الرخصة

هذا المشروع مرخص بموجب رخصة MIT.
