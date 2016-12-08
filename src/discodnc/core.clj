(ns discodnc.core
  (:import [unikl.disco.curves ArrivalCurve ServiceCurve MaxServiceCurve Curve]
           [unikl.disco.network Network Flow Server]
           [unikl.disco.nc AnalysisConfig PmooAnalysis SeparateFlowAnalysis TotalFlowAnalysis
            AnalysisConfig$GammaFlag]
           [unikl.disco.minplus Convolution Deconvolution]
           ))


;; reduce Java names to formulas


(defn β [R T]
  (ServiceCurve/createRateLatency (double T) (double R)))

(defn λ [R]
  (β R 0.0))

(defn δ  [T]
  (ServiceCurve/createDelayedInfiniteBurst (double T)))



(defn conv [a b]
  (Convolution/convolve a b))

(defn deconv [a b]
  (Deconvolution/deconvolve a b))

(conv (λ 5) (δ 5))
;; #object[unikl.disco.curves.ServiceCurve 0x346ea8ba "SC{(0.0,0.0),0.0}"]

(defn γ [r b]
  (ArrivalCurve/createTokenBucket (double r) (double b)))

(defn max
  ([f g]
   (Curve/max f g))
  ([f g & gs]
   (reduce max (max f g) gs)))

(defn min
  ([f g]
   (Curve/min f g))
  ([f g & gs]
   (reduce min (min f g) gs)))


(let [a (min (γ 9 20) (γ 2 76))
      b (max (β 1 0) (β 5 2.4) (β 15 4.8))]
  [(type a) (type b)]
  (deconv (ArrivalCurve. a) (ServiceCurve. b)))
;; #object[unikl.disco.curves.ArrivalCurve 0x30a27ac "AC{(0.0,0.0),0.0;!(0.0,84.8),2.400000000000001;(3.0,92.0),2.0}"]





(comment
  ;; playground from Demo1

  (def arrival-curve (ArrivalCurve/createTokenBucket 0.1e6 (* 0.1 0.1e6)))

  (def service-curve (ServiceCurve/createRateLatency 10.0e6 0.01))

  (def max-service-curve (MaxServiceCurve/createRateLatency 100.0e6 0.001))


  (def network (Network.))

  (def configuration (AnalysisConfig.))


  (def s0 (.addServer network service-curve max-service-curve))

  (doto configuration
    (.setUseGamma AnalysisConfig$GammaFlag/GLOBALLY_ON)
    (.setUseExtraGamma AnalysisConfig$GammaFlag/GLOBALLY_ON))


  (def flow (.addFlow network arrival-curve s0))

  (def tfa (TotalFlowAnalysis. network configuration))


  )
