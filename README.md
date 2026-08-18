# بوصلة القبلة - Qibla Compass

تطبيق أندرويد حديث لتحديد اتجاه القبلة بدقة باستخدام مستشعرات الجهاز ونظام تحديد المواقع (GPS).

## المميزات

- 🧭 **بوصلة دقيقة**: حساب اتجاه القبلة باستخدام صيغة الدائرة العظمى (Great Circle Formula)
- 📍 **تحديد الموقع**: استخدام GPS وشبكة لتحديد موقع المستخدم بدقة
- 🎯 **مؤشر بصري**: سهم دوار يشير مباشرة للكعبة المشرفة
- 📏 **المسافة**: عرض المسافة إلى الكعبة بالكيلومترات
- 🎨 **واجهة حديثة**: تصميم Material 3 مع دعم الوضع الداكن/الفاتح
- 🔧 **معايرة البوصلة**: دعم معايرة المستشعرات لتحسين الدقة

## التقنيات المستخدمة

- **Kotlin** - لغة البرمجة الأساسية
- **Jetpack Compose** - واجهة مستخدم حديثة Declarative UI
- **Clean Architecture + MVVM** - فصل الاهتمامات وقابلية الاختبار
- **Hilt** - حقن التبعيات (Dependency Injection)
- **Coroutines & Flow** - البرمجة غير المتزامنة
- **Material 3** - نظام التصميم الحديث
- **JUnit + MockK + Compose UI Testing** - اختبارات شاملة

## متطلبات البناء

- Android Studio Ladybug (2024.2.1) أو أحدث
- JDK 17
- Android SDK 34 (API Level 34)
- Gradle 8.4+

## هيكل المشروع

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/qibla/compass/
│   │   │   ├── data/           # طبقة البيانات
│   │   │   │   ├── QiblaCalculator.kt      # خوارزمية حساب القبلة
│   │   │   │   ├── QiblaData.kt            # نماذج البيانات
│   │   │   │   └── QiblaRepositoryImpl.kt  # تنفيذ المستودع
│   │   │   ├── domain/         # طبقة النطاق (Domain)
│   │   │   │   └── QiblaRepository.kt      # واجهة المستودع
│   │   │   ├── ui/             # طبقة العرض
│   │   │   │   ├── QiblaViewModel.kt       # ViewModel
│   │   │   │   ├── QiblaScreen.kt          # الشاشة الرئيسية
│   │   │   │   └── MainActivity.kt         # نقطة الدخول
│   │   │   ├── di/             # حقن التبعيات
│   │   │   │   └── RepositoryModule.kt
│   │   │   ├── location/       # خدمات الموقع
│   │   │   └── QiblaApplication.kt         # فئة التطبيق
│   │   ├── res/                # الموارد
│   │   └── AndroidManifest.xml
│   ├── test/                   # اختبارات الوحدة
│   │   └── java/com/qibla/compass/
│   │       ├── data/QiblaCalculatorTest.kt
│   │       └── ui/QiblaViewModelTest.kt
│   └── androidTest/            # اختبارات الواجهة
│       └── java/com/qibla/compass/ui/QiblaScreenTest.kt
├── build.gradle.kts
└── proguard-rules.pro
```

## خوارزمية حساب القبلة

يستخدم التطبيق **صيغة الدائرة العظمى (Great Circle Formula)** لحساب اتجاه القبلة بدقة رياضية:

```kotlin
// حساب المحمل (Bearing) من نقطة إلى نقطة على الكرة الأرضية
val y = sin(Δλ) * cos(lat2)
val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(Δλ)
bearing = atan2(y, x) * 180/π
```

حيث:
- `lat1, lon1`: إحداثيات المستخدم
- `lat2, lon2`: إحداثيات الكعبة (21.422508°, 39.826184°)

## الأذونات المطلوبة

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.BODY_SENSORS" />
```

## المستشعرات المستخدمة

- **Accelerometer** (تسارع) - لتحديد اتجاه الجاذبية
- **Magnetic Field** (مجال مغناطيسي) - لتحديد الشمال المغناطيسي

## التشغيل

1. افتح المشروع في Android Studio
2. انتظر مزامنة Gradle
3. شغّل التطبيق على جهاز حقيقي (المحاكي لا يدعم المستشعرات بشكل كامل)
4. امنح أذونات الموقع عند الطلب
5. حرّك الجهاز بشكل "8" لمعايرة البوصلة إذا لزم الأمر

## الاختبارات

```bash
# تشغيل اختبارات الوحدة
./gradlew test

# تشغيل اختبارات الواجهة (يتطلب جهاز/محاكي متصل)
./gradlew connectedAndroidTest

# تشغيل جميع الاختبارات
./gradlew test connectedAndroidTest
```

## دقة القبلة

| دقة الموقع | مستوى الدقة | الوصف |
|------------|-------------|--------|
| ≤ 10 متر | عالية | GPS نشط، دقة ممتازة |
| 10-50 متر | متوسطة | GPS/شبكة، دقة جيدة |
| > 50 متر | منخفضة | شبكة فقط، دقة تقريبية |

## المساهمة

المساهمات مرحب بها! يرجى:
1. عمل Fork للمشروع
2. إنشاء فرع للميزة الجديدة
3. إضافة الاختبارات اللازمة
4. إرسال Pull Request

## الترخيص

هذا المشروع مرخص تحت رخصة MIT - راجع ملف [LICENSE](LICENSE) للتفاصيل.

## الشكر

- صيغة الدائرة العظمى لحساب الاتجاهات على الكرة الأرضية
- مجتمع مطوري أندرويد العربي
- مكتبات Google Jetpack و Material Design