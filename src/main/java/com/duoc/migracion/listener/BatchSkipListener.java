package com.duoc.migracion.listener;

import com.duoc.migracion.dto.CuentaAnualCsvDto;
import com.duoc.migracion.policy.BatchRecordPolicy;
import org.springframework.batch.core.SkipListener;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class BatchSkipListener implements SkipListener<Object, Object> {

    private final BatchRecordPolicy batchRecordPolicy;

    public BatchSkipListener(BatchRecordPolicy batchRecordPolicy) {
        this.batchRecordPolicy = batchRecordPolicy;
    }

    @Override
    public void onSkipInRead(@NonNull Throwable throwable) {
        logSkip("lectura", null, throwable);
    }

    @Override
    public void onSkipInWrite(@Nullable Object item, @NonNull Throwable throwable) {
        logSkip("escritura", item, throwable);
    }

    @Override
    public void onSkipInProcess(@Nullable Object item, @NonNull Throwable throwable) {
        logSkip("procesamiento", item, throwable);
    }

    private void logSkip(String etapa, Object item, Throwable throwable) {
        String category = batchRecordPolicy.classify(throwable);

        System.err.println("--------------------------------------------------");
        System.err.println("[SkipListener] Registro omitido en " + etapa);
        System.err.println("-> Causa: " + category + " - " + throwable.getMessage());

        if (item != null) {
            if (item instanceof CuentaAnualCsvDto dto) {
                System.err.println("-> Cuenta ID   : " + dto.getCuentaId());
                System.err.println("-> Fecha       : " + dto.getFecha());
                System.err.println("-> Transacción : " + dto.getTransaccion());
                System.err.println("-> Monto       : " + dto.getMonto());
                System.err.println("-> Descripción : " + dto.getDescripcion());
            } else {
                System.err.println("-> Objeto      : " + item);
            }
        }
        System.err.println("--------------------------------------------------");
    }
}