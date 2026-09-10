<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Client;
use Illuminate\Http\Request;
use Illuminate\Validation\Rule;

class ClientController extends Controller
{
    /**
     * Display Clients
     */
    public function index(Request $request)
    {
        $query = Client::query()->withCount('activations');

        // Search
        if ($request->filled('search')) {

            $search = $request->search;

            $query->where(function ($q) use ($search) {

                $q->where('client_code', 'LIKE', "%{$search}%")
                    ->orWhere('name', 'LIKE', "%{$search}%")
                    ->orWhere('company', 'LIKE', "%{$search}%")
                    ->orWhere('mobile', 'LIKE', "%{$search}%")
                    ->orWhere('email', 'LIKE', "%{$search}%")
                    ->orWhere('city', 'LIKE', "%{$search}%");

            });

        }

        // Plan Filter
        if ($request->filled('plan')) {
            $query->where('plan', $request->plan);
        }

        // Status Filter
        if ($request->filled('status')) {
            $query->where('status', $request->status);
        }

        $clients = $query
            ->latest()
            ->paginate(10)
            ->withQueryString();

        return view('clients.index', compact('clients'));
    }

    /**
     * Show Create Form
     */
    public function create()
    {
        return view('clients.create');
    }

    /**
     * Store Client
     */
    public function store(Request $request)
    {
        $validated = $request->validate([

            'name' => 'required|string|max:255',

            'company' => 'nullable|string|max:255',

            'mobile' => 'required|string|max:20',

            'alternate_mobile' => 'nullable|string|max:20',

            'email' => 'nullable|email|max:255',

            'gst_number' => 'nullable|string|max:50',

            'city' => 'nullable|string|max:100',

            'state' => 'nullable|string|max:100',

            'address' => 'nullable|string',

            'plan' => ['required', Rule::in([
                'Basic',
                'Professional',
                'Enterprise'
            ])],

            'expiry_date' => 'nullable|date',

            'status' => ['required', Rule::in([
                'Active',
                'Inactive'
            ])],

            'notes' => 'nullable|string',

        ]);

        // Generate Client Code
        $nextId = (Client::max('id') ?? 0) + 1;

        $validated['client_code'] = 'RP' . str_pad($nextId, 6, '0', STR_PAD_LEFT);

        Client::create($validated);

        return redirect()
            ->route('clients.index')
            ->with('success', 'Client added successfully.');
    }

    /**
     * Show Client
     */
    public function show(Client $client)
    {
        $client->loadCount('activations');

        $latestActivations = $client->activations()
            ->latest()
            ->take(10)
            ->get();

        return view('clients.show', compact(
            'client',
            'latestActivations'
        ));
    }

    /**
     * Edit Form
     */
    public function edit(Client $client)
    {
        return view('clients.edit', compact('client'));
    }

    /**
     * Update Client
     */
    public function update(Request $request, Client $client)
    {
        $validated = $request->validate([

            'name' => 'required|string|max:255',

            'company' => 'nullable|string|max:255',

            'mobile' => 'required|string|max:20',

            'alternate_mobile' => 'nullable|string|max:20',

            'email' => 'nullable|email|max:255',

            'gst_number' => 'nullable|string|max:50',

            'city' => 'nullable|string|max:100',

            'state' => 'nullable|string|max:100',

            'address' => 'nullable|string',

            'plan' => ['required', Rule::in([
                'Basic',
                'Professional',
                'Enterprise'
            ])],

            'expiry_date' => 'nullable|date',

            'status' => ['required', Rule::in([
                'Active',
                'Inactive'
            ])],

            'notes' => 'nullable|string',

        ]);

        $client->update($validated);

        return redirect()
            ->route('clients.index')
            ->with('success', 'Client updated successfully.');
    }

    /**
     * Delete Client
     */
    public function destroy(Client $client)
    {
        // Prevent deletion if activations exist
        if ($client->activations()->count() > 0) {

            return back()->with(
                'error',
                'Client has activations and cannot be deleted.'
            );
        }

        $client->delete();

        return redirect()
            ->route('clients.index')
            ->with('success', 'Client deleted successfully.');
    }
}