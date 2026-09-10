<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Client;
use App\Models\License;
use Illuminate\Http\Request;
use Illuminate\Support\Str;

class LicenseController extends Controller
{
    public function index()
    {
        $licenses = License::with('client')
            ->latest()
            ->paginate(10);

        return view('licenses.index', compact('licenses'));
    }

    public function create()
    {
        $clients = Client::orderBy('name')->get();

        return view('licenses.create', compact('clients'));
    }

    public function store(Request $request)
    {
        $request->validate([
            'client_id' => 'required|exists:clients,id',
            'product_name' => 'required|string|max:255',
            'plan' => 'required',
            'purchase_date' => 'required|date',
            'expiry_date' => 'required|date|after_or_equal:purchase_date',
        ]);

        License::create([
            'client_id' => $request->client_id,
            'license_key' => strtoupper(Str::random(8)) . '-' . strtoupper(Str::random(8)),
            'product_name' => $request->product_name,
            'plan' => $request->plan,
            'device_limit' => $request->device_limit ?? 1,
            'activated_devices' => 0,
            'purchase_date' => $request->purchase_date,
            'expiry_date' => $request->expiry_date,
            'status' => 'Active',
            'remarks' => $request->remarks,
        ]);

        return redirect()->route('licenses.index')
            ->with('success', 'License created successfully.');
    }

    public function edit(License $license)
    {
        $clients = Client::orderBy('name')->get();

        return view('licenses.edit', compact('license', 'clients'));
    }

    public function update(Request $request, License $license)
    {
        // We'll implement this in the next step
    }

    public function destroy(License $license)
    {
        $license->delete();

        return redirect()->route('licenses.index')
            ->with('success', 'License deleted successfully.');
    }
}