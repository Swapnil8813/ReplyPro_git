<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\API\ActivationApiController;
use App\Http\Controllers\API\CallSyncController;
use App\Http\Controllers\API\SettingsController;
Route::get('/status', function () {
    return response()->json([
        'status' => true,
        'message' => 'ReplyPro API Running',
        'version' => '1.0'
    ]);
});
Route::post('/call/sync', [CallSyncController::class, 'sync']);
Route::post('/check-activation', [ActivationApiController::class, 'check']);
Route::get('/sync/{mobile}', [ActivationApiController::class, 'sync']);

Route::get('/settings/{mobile}', [SettingsController::class, 'get']);

Route::post('/settings/update', [SettingsController::class, 'update']);

Route::post('/settings/upload', [SettingsController::class, 'upload']);