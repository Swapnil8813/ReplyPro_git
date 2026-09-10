<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Activation;
use App\Models\Client;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Validation\Rule;

class ActivationController extends Controller
{
    /**
     * Activation Listing
     */
    public function index(Request $request)
    {
        // Automatically update expired licenses
        Activation::whereDate('expiry_date', '<', today())
            ->where('status', 'Active')
            ->update([
                'status' => 'Expired'
            ]);

        $query = Activation::with('client');

        // Search
        if ($request->filled('search')) {

            $search = $request->search;

            $query->where(function ($q) use ($search) {

                $q->where('mobile_number', 'LIKE', "%{$search}%")
                    ->orWhereHas('client', function ($client) use ($search) {

                        $client->where('name', 'LIKE', "%{$search}%")
                            ->orWhere('client_code', 'LIKE', "%{$search}%");

                    });

            });

        }

        // Status
        if ($request->filled('status')) {

            $query->where('status', $request->status);

        }

        // WhatsApp
        if ($request->filled('whatsapp')) {

            $query->where('whatsapp_enabled', $request->whatsapp);

        }

        // SMS
        if ($request->filled('sms')) {

            $query->where('sms_enabled', $request->sms);

        }

        $activations = $query
            ->latest()
            ->paginate(10)
            ->withQueryString();

        return view('activations.index', compact('activations'));
    }

    /**
     * Create Form
     */
    public function create()
    {
        $clients = Client::orderBy('name')->get();

        return view('activations.create', compact('clients'));
    }

    /**
     * Store Activation
     */
    public function store(Request $request)
    {
        $validated = $request->validate([

            'client_id' => 'required|exists:clients,id',

            'mobile_number' => 'required|digits:10|unique:activations,mobile_number',

            'start_date' => 'required|date',

            'expiry_date' => 'required|date|after_or_equal:start_date',

            'whatsapp_message' => 'nullable|string',

            'sms_message' => 'nullable|string',

            'remarks' => 'nullable|string',

            'whatsapp_attachment' => 'nullable|file|mimes:jpg,jpeg,png,pdf,mp4,mov,avi|max:51200',

        ]);

        $attachment = null;
        $type = null;

        if ($request->hasFile('whatsapp_attachment')) {

            $file = $request->file('whatsapp_attachment');

            $attachment = $file->store('attachments', 'public');

            $extension = strtolower($file->getClientOriginalExtension());

            if (in_array($extension, ['jpg', 'jpeg', 'png'])) {

                $type = 'image';

            } elseif (in_array($extension, ['mp4', 'mov', 'avi'])) {

                $type = 'video';

            } else {

                $type = 'pdf';

            }

        }

        Activation::create([

            'client_id' => $validated['client_id'],

            'mobile_number' => $validated['mobile_number'],

            'start_date' => $validated['start_date'],

            'expiry_date' => $validated['expiry_date'],

            'status' => today()->gt($validated['expiry_date'])
                ? 'Expired'
                : 'Active',

            'whatsapp_enabled' => $request->boolean('whatsapp_enabled'),

            'whatsapp_message' => $request->whatsapp_message,

            'whatsapp_attachment' => $attachment,

            'attachment_type' => $type,

            'sms_enabled' => $request->boolean('sms_enabled'),

            'sms_message' => $request->sms_message,

            'remarks' => $request->remarks,

        ]);

        return redirect()
            ->route('activations.index')
            ->with('success', 'Activation created successfully.');
    }

    /**
     * Activation Details
     */
    public function show(Activation $activation)
    {
        $activation->load('client');

        return view('activations.show', compact('activation'));
    }

    /**
     * Edit
     */
    public function edit(Activation $activation)
    {
        $clients = Client::orderBy('name')->get();

        return view('activations.edit', compact(
            'activation',
            'clients'
        ));
    }

    /**
     * Update
     */
  public function update(Request $request, Activation $activation)
{
    $validated = $request->validate([

        'client_id' => 'required|exists:clients,id',

        'mobile_number' => [
            'required',
            'digits:10',
            Rule::unique('activations')->ignore($activation->id),
        ],

        'start_date' => 'required|date',

        'expiry_date' => 'required|date|after_or_equal:start_date',

        'whatsapp_attachment' => 'nullable|file|mimes:jpg,jpeg,png,pdf,mp4,mov,avi|max:51200',

        'whatsapp_message' => 'nullable|string',

        'sms_message' => 'nullable|string',

        'remarks' => 'nullable|string',

    ]);

    $attachment = $activation->whatsapp_attachment;
    $type = $activation->attachment_type;

    if ($request->hasFile('whatsapp_attachment')) {

        // Delete old attachment if it exists
        if (!empty($activation->whatsapp_attachment)) {

            Storage::disk('public')->delete($activation->whatsapp_attachment);

        }

        $file = $request->file('whatsapp_attachment');

        // Store new file
        $attachment = $file->store('attachments', 'public');

        $extension = strtolower($file->getClientOriginalExtension());

        if (in_array($extension, ['jpg', 'jpeg', 'png'])) {

            $type = 'image';

        } elseif (in_array($extension, ['mp4', 'mov', 'avi'])) {

            $type = 'video';

        } else {

            $type = 'pdf';

        }

    }

    $activation->client_id = $validated['client_id'];
    $activation->mobile_number = $validated['mobile_number'];
    $activation->start_date = $validated['start_date'];
    $activation->expiry_date = $validated['expiry_date'];
    $activation->status = today()->gt($validated['expiry_date']) ? 'Expired' : 'Active';

    $activation->whatsapp_enabled = $request->boolean('whatsapp_enabled');
    $activation->whatsapp_message = $request->whatsapp_message;
    $activation->whatsapp_attachment = $attachment;
    $activation->attachment_type = $type;

    $activation->sms_enabled = $request->boolean('sms_enabled');
    $activation->sms_message = $request->sms_message;

    $activation->remarks = $request->remarks;

    $activation->save();

    return redirect()
        ->route('activations.index')
        ->with('success', 'Activation updated successfully.');
}
    /**
     * Delete
     */
    public function destroy(Activation $activation)
    {
        if ($activation->whatsapp_attachment) {

            Storage::disk('public')->delete(
                $activation->whatsapp_attachment
            );

        }

        $activation->delete();

        return redirect()
            ->route('activations.index')
            ->with('success', 'Activation deleted successfully.');
    }
    public function resetDevice(Activation $activation)
{
    $activation->update([
        'device_id' => null,
        'device_name' => null,
        'app_version' => null,
        'last_seen' => null,
        'blocked' => false,
    ]);

    return redirect()
        ->back()
        ->with('success', 'Device has been reset successfully.');
}
public function blockDevice(Activation $activation)
{
    $activation->update([
        'blocked' => true,
    ]);

    return redirect()
        ->back()
        ->with('success', 'Device has been blocked successfully.');
}
public function unblockDevice(Activation $activation)
{
    $activation->update([
        'blocked' => false,
    ]);

    return redirect()
        ->back()
        ->with('success', 'Device has been unblocked successfully.');
}
}