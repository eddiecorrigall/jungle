Types
=====

| Name      | Java Name | Min       | Max      |
|-----------|-----------|-----------|----------|
| `boolean` | `boolean` | `false`   | `true`   |
| `unicode` | `char`    | `0`       | `2^16-1` |
| `i8`      | `byte`    | `-128`    | `127`    |
| `i16`     | `short`   | `-2^15`   | `2^15-1` |
| `i32`     | `int`     | `-2^31`   | `2^31-1` |
| `i64`     | `long`    | `-2^63`   | `2^63-1` |
| `f32`     | `float`   |           |          |
| `f64`     | `double`  |           |          |

## Implicit Conversion

- All integer and floating-point operations will preserve accuracy by converting to the largest precision type

```
x = 123     # i32 (default integer)
y = x + 0.0 # f32 (default float) implicit cast
```

## Explicit Conversion

- To limit precision, use explicit casting, for example:
    - Casting from floating-point to integer
    - Casting from i32 to i8

Syntax Idea:

```
x = 123     # i32 (default integer)
y = i8(x)   # i8 decrease precision
z = f64(x)  # f64 increase precision
```

Unicode Idea:

```
i = ordinal('0')  # return index of the unicode
u = unicode(48)   # return unicode value of numeric
```
