cljreadings
===========

Web application in Clojure for device readings. It uses immutant for deployment, which is Clojure's interface to JBoss.

## Installment of lein-immutant

```
cat > ~/.lein/profiles.clj
{:user {:plugins [[lein-immutant "1.0.0.beta1"]]}}
```

## Deployment with immutant
- lein immutant install LATEST
- lein immutant deploy
- lein immutant run
