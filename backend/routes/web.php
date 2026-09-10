<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\ProfileController;

use App\Http\Controllers\Admin\DashboardController;
use App\Http\Controllers\Admin\CustomerController;
use App\Http\Controllers\Admin\ClientController;
use App\Http\Controllers\Admin\LicenseController;
use App\Http\Controllers\Admin\DeviceController;
use App\Http\Controllers\Admin\ActivationController;

Route::get('/', function () {
    return redirect()->route('login');
});

Route::middleware(['auth'])->group(function () {

    Route::get('/dashboard', [DashboardController::class, 'index'])
        ->name('dashboard');
Route::post('/activations/{activation}/reset-device', [ActivationController::class, 'resetDevice'])
    ->name('activations.resetDevice');

Route::post('/activations/{activation}/block-device', [ActivationController::class, 'blockDevice'])
    ->name('activations.blockDevice');

Route::post('/activations/{activation}/unblock-device', [ActivationController::class, 'unblockDevice'])
    ->name('activations.unblockDevice');
    Route::resource('clients', ClientController::class);
    Route::resource('activations', ActivationController::class);
    Route::resource('customers', CustomerController::class);
    Route::resource('licenses', LicenseController::class);
    Route::resource('devices', DeviceController::class);

    Route::get('/profile', [ProfileController::class, 'edit'])
        ->name('profile.edit');

    Route::patch('/profile', [ProfileController::class, 'update'])
        ->name('profile.update');

    Route::delete('/profile', [ProfileController::class, 'destroy'])
        ->name('profile.destroy');
});

require __DIR__.'/auth.php';