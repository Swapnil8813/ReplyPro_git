<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Client;
use App\Models\Activation;
use Carbon\Carbon;

class DashboardController extends Controller
{
    public function index()
    {
        $today = Carbon::today();

        $totalClients = Client::count();

        $totalActivations = Activation::count();

        $activeLicenses = Activation::whereDate('expiry_date', '>=', $today)->count();

        $expiredLicenses = Activation::whereDate('expiry_date', '<', $today)->count();

        $expiringSoon = Activation::whereBetween('expiry_date', [
            $today,
            $today->copy()->addDays(7)
        ])->count();

        $monthlyActivations = Activation::whereMonth('created_at', now()->month)
            ->whereYear('created_at', now()->year)
            ->count();

        $latestActivations = Activation::with('client')
            ->latest()
            ->take(10)
            ->get();

        return view('dashboard.index', compact(
            'totalClients',
            'totalActivations',
            'activeLicenses',
            'expiredLicenses',
            'expiringSoon',
            'monthlyActivations',
            'latestActivations'
        ));
    }
}