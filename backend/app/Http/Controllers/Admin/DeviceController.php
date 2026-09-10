<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Device;

class DeviceController extends Controller
{
    public function index()
    {
        $devices = Device::with('license.client')
            ->latest()
            ->paginate(10);

        return view('devices.index', compact('devices'));
    }

    public function create()
    {
        return redirect()->route('devices.index');
    }
}