package com.github.aesteve.fertx.util

import com.github.aesteve.fertx.Response

private[util] abstract class ApplyConverterInstances {
  // FIXME : this shouldn't be needed or this should be handling () => Response
  implicit def hac0: ApplyConverter[Unit] { type In = () => Response } = new ApplyConverter[Unit] {
    type In = () => Response
    def apply(fn: () => Response): Unit ⇒ Response = { _ =>
      fn()
    }
  }


  implicit def hac1[T1]: ApplyConverter[Tuple1[T1]] { type In = (T1) ⇒ Response } = new ApplyConverter[Tuple1[T1]] {
    type In = (T1) ⇒ Response
    def apply(fn: In): (Tuple1[T1]) ⇒ Response = {
      case Tuple1(t1) ⇒ fn(t1)
    }
  }
  implicit def hac2[T1, T2]: ApplyConverter[(T1, T2)] { type In = (T1, T2) ⇒ Response } = new ApplyConverter[(T1, T2)] {
    type In = (T1, T2) ⇒ Response
    def apply(fn: In): ((T1, T2)) ⇒ Response = {
      case Tuple2(t1, t2) ⇒ fn(t1, t2)
    }
  }
  implicit def hac3[T1, T2, T3]: ApplyConverter[(T1, T2, T3)] { type In = (T1, T2, T3) ⇒ Response } = new ApplyConverter[(T1, T2, T3)] {
    type In = (T1, T2, T3) ⇒ Response
    def apply(fn: In): ((T1, T2, T3)) ⇒ Response = {
      case Tuple3(t1, t2, t3) ⇒ fn(t1, t2, t3)
    }
  }
  implicit def hac4[T1, T2, T3, T4]: ApplyConverter[(T1, T2, T3, T4)] { type In = (T1, T2, T3, T4) ⇒ Response } = new ApplyConverter[(T1, T2, T3, T4)] {
    type In = (T1, T2, T3, T4) ⇒ Response
    def apply(fn: In): ((T1, T2, T3, T4)) ⇒ Response = {
      case Tuple4(t1, t2, t3, t4) ⇒ fn(t1, t2, t3, t4)
    }
  }
  implicit def hac5[T1, T2, T3, T4, T5]: ApplyConverter[(T1, T2, T3, T4, T5)] { type In = (T1, T2, T3, T4, T5) ⇒ Response } = new ApplyConverter[(T1, T2, T3, T4, T5)] {
    type In = (T1, T2, T3, T4, T5) ⇒ Response
    def apply(fn: In): ((T1, T2, T3, T4, T5)) ⇒ Response = {
      case Tuple5(t1, t2, t3, t4, t5) ⇒ fn(t1, t2, t3, t4, t5)
    }
  }
  implicit def hac6[T1, T2, T3, T4, T5, T6]: ApplyConverter[(T1, T2, T3, T4, T5, T6)] { type In = (T1, T2, T3, T4, T5, T6) ⇒ Response } = new ApplyConverter[(T1, T2, T3, T4, T5, T6)] {
    type In = (T1, T2, T3, T4, T5, T6) ⇒ Response
    def apply(fn: In): ((T1, T2, T3, T4, T5, T6)) ⇒ Response = {
      case Tuple6(t1, t2, t3, t4, t5, t6) ⇒ fn(t1, t2, t3, t4, t5, t6)
    }
  }
  implicit def hac7[T1, T2, T3, T4, T5, T6, T7]: ApplyConverter[(T1, T2, T3, T4, T5, T6, T7)] { type In = (T1, T2, T3, T4, T5, T6, T7) ⇒ Response } = new ApplyConverter[(T1, T2, T3, T4, T5, T6, T7)] {
    type In = (T1, T2, T3, T4, T5, T6, T7) ⇒ Response
    def apply(fn: In): ((T1, T2, T3, T4, T5, T6, T7)) ⇒ Response = {
      case Tuple7(t1, t2, t3, t4, t5, t6, t7) ⇒ fn(t1, t2, t3, t4, t5, t6, t7)
    }
  }
  implicit def hac8[T1, T2, T3, T4, T5, T6, T7, T8]: ApplyConverter[(T1, T2, T3, T4, T5, T6, T7, T8)] { type In = (T1, T2, T3, T4, T5, T6, T7, T8) ⇒ Response } = new ApplyConverter[(T1, T2, T3, T4, T5, T6, T7, T8)] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8) ⇒ Response
    def apply(fn: In): ((T1, T2, T3, T4, T5, T6, T7, T8)) ⇒ Response = {
      case Tuple8(t1, t2, t3, t4, t5, t6, t7, t8) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8)
    }
  }
  implicit def hac9[T1, T2, T3, T4, T5, T6, T7, T8, T9]: ApplyConverter[(T1, T2, T3, T4, T5, T6, T7, T8, T9)] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9) ⇒ Response } = new ApplyConverter[Tuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9) ⇒ Response
    def apply(fn: In): ((T1, T2, T3, T4, T5, T6, T7, T8, T9)) ⇒ Response = {
      case Tuple9(t1, t2, t3, t4, t5, t6, t7, t8, t9) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9)
    }
  }
  implicit def hac10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10]: ApplyConverter[Tuple10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) ⇒ Response } = new ApplyConverter[Tuple10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) ⇒ Response
    def apply(fn: In): (Tuple10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10]) ⇒ Response = {
      case Tuple10(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10)
    }
  }
  implicit def hac11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11]: ApplyConverter[Tuple11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) ⇒ Response } = new ApplyConverter[Tuple11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) ⇒ Response
    def apply(fn: In): (Tuple11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11]) ⇒ Response = {
      case Tuple11(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11)
    }
  }
  implicit def hac12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12]: ApplyConverter[Tuple12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) ⇒ Response } = new ApplyConverter[Tuple12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) ⇒ Response
    def apply(fn: In): (Tuple12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12]) ⇒ Response = {
      case Tuple12(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12)
    }
  }
  implicit def hac13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13]: ApplyConverter[Tuple13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) ⇒ Response } = new ApplyConverter[Tuple13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) ⇒ Response
    def apply(fn: In): (Tuple13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13]) ⇒ Response = {
      case Tuple13(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13)
    }
  }
  implicit def hac14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14]: ApplyConverter[Tuple14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) ⇒ Response } = new ApplyConverter[Tuple14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) ⇒ Response
    def apply(fn: In): (Tuple14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14]) ⇒ Response = {
      case Tuple14(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14)
    }
  }
  implicit def hac15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15]: ApplyConverter[Tuple15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) ⇒ Response } = new ApplyConverter[Tuple15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) ⇒ Response
    def apply(fn: In): (Tuple15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15]) ⇒ Response = {
      case Tuple15(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15)
    }
  }
  implicit def hac16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16]: ApplyConverter[Tuple16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) ⇒ Response } = new ApplyConverter[Tuple16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) ⇒ Response
    def apply(fn: In): (Tuple16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16]) ⇒ Response = {
      case Tuple16(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16)
    }
  }
  implicit def hac17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17]: ApplyConverter[Tuple17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) ⇒ Response } = new ApplyConverter[Tuple17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) ⇒ Response
    def apply(fn: In): (Tuple17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17]) ⇒ Response = {
      case Tuple17(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
    }
  }
  implicit def hac18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18]: ApplyConverter[Tuple18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) ⇒ Response } = new ApplyConverter[Tuple18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) ⇒ Response
    def apply(fn: In): (Tuple18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18]) ⇒ Response = {
      case Tuple18(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18)
    }
  }
  implicit def hac19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19]: ApplyConverter[Tuple19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) ⇒ Response } = new ApplyConverter[Tuple19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) ⇒ Response
    def apply(fn: In): (Tuple19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19]) ⇒ Response = {
      case Tuple19(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19)
    }
  }
  implicit def hac20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20]: ApplyConverter[Tuple20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) ⇒ Response } = new ApplyConverter[Tuple20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) ⇒ Response
    def apply(fn: In): (Tuple20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20]) ⇒ Response = {
      case Tuple20(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20)
    }
  }
  implicit def hac21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21]: ApplyConverter[Tuple21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) ⇒ Response } = new ApplyConverter[Tuple21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) ⇒ Response
    def apply(fn: In): (Tuple21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21]) ⇒ Response = {
      case Tuple21(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21)
    }
  }
  implicit def hac22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22]: ApplyConverter[Tuple22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22]] { type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) ⇒ Response } = new ApplyConverter[Tuple22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22]] {
    type In = (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) ⇒ Response
    def apply(fn: In): (Tuple22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22]) ⇒ Response = {
      case Tuple22(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22) ⇒ fn(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22)
    }
  }
}
