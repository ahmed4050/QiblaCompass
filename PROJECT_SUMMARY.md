# ملخص مشروع بوصلة القبلة (Qibla Compass)

> آخر تحديث: 19 أغسطس 2026

---

## 1. نظرة عامة

تطبيق أندرويد لتحديد **اتجاه القبلة** بدقة باستخدام مستشعرات الجهاز (Accelerometer + Magnetic Field) ونظام تحديد المواقع (GPS). يعرض التطبيق بوصلة رقمية حية مع سهم دوار يشير مباشرة إلى الكعبة المشرفة، بالإضافة إلى المسافة بالكيلومترات.

| البند | القيمة |
|-------|--------|
| اسم الحزمة | `com.qibla.compass` |
| اسم التطبيق | بوصلة القبلة |
| الحد الأدنى للـ API | 24 (Android 7.0) |
| الهدف (Target) | 34 (Android 14) |
| لغة البرمجة | Kotlin |
| الواجهة | Jetpack Compose (Material 3) |

---

## 2. الحالة الحالية

| العنصر | الحالة |
|--------|--------|
| الكود المصدري | ✅ مكتمل |
| البناء المحلي | ⛔ غير ممكن (لا يوجد Java/Android Studio على الجهاز) |
| البناء عبر GitHub Actions | 🔄 قيد الإصلاح (أخطاء تجميع تُعالج تباعاً) |
| مستودع GitHub | [ahmed4050/QiblaCompass](https://github.com/ahmed4050/QiblaCompass) |
| آخر كود مرفوع | `baf031d` |

### سجل أخطاء البناء وحلولها

| # | المشكلة | الحل |
|---|---------|------|
| 1 | `org.jetbrains.kotlin.plugin.compose` 1.9.22 غير موجود | ترقية Kotlin إلى 2.0.0 |
| 2 | AGP 8.4 يتطلب Gradle 8.6+ | ترقية الـ wrapper إلى 8.6 |
| 3 | Compose BOM 1.6.1 غير موجود | استخدام `2024.02.02` |
| 4 | خيارات build قديمة | إزالة `enableR8.libraries` و `packagingOptions` |
| 5 | أخطاء `android.graphics.Path` و `size.toPx()` | استخدام Compose Path وحساب الأبعاد داخل DrawScope |
| 6 | استيراد `animateFloatAsState` خاطئ | التصحيح إلى `animation.core.animateFloatAsState` |
| 7 | `receiveAsFlow`/`sharingStarted` غير موجودة | تبسيط إلى `MutableStateFlow` |
| 8 | `AccuracyLevel` و `Dp` غير مستوردين | إضافة الاستيرادات |
| 9 | `MaterialTheme.colorScheme` داخل Canvas | نقل الألوان خارج الـ Canvas |
| 10 | `actionPerformed` في `showSnackbar` | استخدام `SnackbarResult` |

---

## 3. بنية المشروع (Clean Architecture + MVVM)

```
app/src/main/java/com/qibla/compass/
├── data/                        # طبقة البيانات
│   ├── QiblaCalculator.kt       # خوارزمية حساب القبلة (Great Circle)
│   ├── QiblaData.kt             # نماذج البيانات + AccuracyLevel
│   └── QiblaRepositoryImpl.kt   # تنفيذ المستودع (مستشعرات + GPS)
├── domain/                      # طبقة النطاق
│   └── QiblaRepository.kt       # واجهة المستودع
├── ui/                          # طبقة العرض
│   ├── MainActivity.kt          # نقطة الدخول + طلب الأذونات
│   ├── QiblaViewModel.kt        # ViewModel
│   ├── QiblaScreen.kt           # الشاشة الرئيسية (البوصلة)
│   └── theme/                   # الألوان والخطوط والأشكال
├── di/                          # حقن التبعيات (Hilt)
│   └── RepositoryModule.kt
└── QiblaApplication.kt          # فئة التطبيق (@HiltAndroidApp)
```

### الاختبارات
- `QiblaCalculatorTest.kt` — اختبارات وحدة لخوارزمية الحساب (12 اختباراً)
- `QiblaViewModelTest.kt` — اختبارات الـ ViewModel (MockK)
- `QiblaScreenTest.kt` — اختبارات واجهة Compose

---

## 4. التقنيات والإصدارات

| التقنية | الإصدار |
|---------|---------|
| Kotlin | 2.0.0 |
| Android Gradle Plugin (AGP) | 8.4.0 |
| Gradle Wrapper | 8.6 |
| Compose BOM | 2024.02.02 |
| Material 3 | 1.2.1 |
| Hilt | 2.48 |
| Coroutines | 1.8.0 |
| JDK | 17 |

---

## 5. الميزات الرئيسية

- 🧭 **بوصلة حية**: دوران سلس مع حركة الجهاز
- 🎯 **سهم القبلة**: يشير مباشرة للكعبة (21.422508°, 39.826184°)
- 📍 **تحديد الموقع**: GPS + Network Provider
- 📏 **المسافة**: عرض المسافة للكعبة بالكيلومترات
- 📊 **مستوى الدقة**: High / Medium / Low حسب دقة الموقع
- 🌗 **وضع داكن/فاتح**: Material 3
- 🔔 **معالجة الأخطاء**: Snackbar مع زر "إعادة المحاولة"

---

## 6. الأذونات

```xml
ACCESS_FINE_LOCATION      — الموقع الدقيق (GPS)
ACCESS_COARSE_LOCATION    — موقع تقريبي (شبكة)
```

## 7. المستشعرات

- **Accelerometer** (TYPE_ACCELEROMETER) — تحديد اتجاه الجاذبية
- **Magnetic Field** (TYPE_MAGNETIC_FIELD) — تحديد الشمال المغناطيسي

---

## 8. خطوات بناء الـ APK (عبر GitHub Actions)

1. ارفع التغييرات: `git push origin main`
2. انتظر 5-7 دقائق في https://github.com/ahmed4050/QiblaCompass/actions
3. عند نجاح البناء: افتح الـ Run الأخير → **Artifacts** → `QiblaCompass-Debug-APK.zip`
4. فك الضغط وثبّت `app-debug.apk` على الهاتف

### التثبيت على الهاتف
1. فعّل "تثبيت تطبيقات من مصادر غير معروفة"
2. افتح `app-debug.apk` وثبّته
3. امنح إذن الموقع عند الطلب
4. إذا كانت البوصلة غير دقيقة، حرّك الجهاز بشكل "8" لمعايرة المستشعرات

---

## 9. ملاحظات أمان (مهمة)

> ⚠️ يجب حذف أي GitHub Token مكشوف من:
> https://github.com/settings/tokens

---

## 10. الخطة القادمة

- [ ] إصلاح أخطاء التجميع المتبقية (إن وجدت) في `QiblaScreen.kt`
- [ ] نجاح البناء في GitHub Actions
- [ ] تسليم `app-debug.apk` للمستخدم
- [ ] (اختياري) بناء نسخة Release موقّعة