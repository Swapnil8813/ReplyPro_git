<x-app-layout>
    <x-slot name="header">
        <h2 class="font-semibold text-xl text-gray-800 leading-tight">
            Add Customer
        </h2>
    </x-slot>

    <div class="py-6">
        <div class="max-w-4xl mx-auto sm:px-6 lg:px-8">

            <div class="bg-white shadow rounded-lg p-6">

                <form action="{{ route('customers.store') }}" method="POST">
                    @csrf

                    <div class="mb-4">
                        <label class="block font-medium mb-2">Company Name</label>
                        <input type="text"
                               name="company_name"
                               class="w-full border rounded-lg p-2">
                    </div>

                    <div class="mb-4">
                        <label class="block font-medium mb-2">Owner Name</label>
                        <input type="text"
                               name="owner_name"
                               class="w-full border rounded-lg p-2">
                    </div>

                    <div class="mb-4">
                        <label class="block font-medium mb-2">Mobile</label>
                        <input type="text"
                               name="mobile"
                               class="w-full border rounded-lg p-2">
                    </div>

                    <div class="mb-4">
                        <label class="block font-medium mb-2">Email</label>
                        <input type="email"
                               name="email"
                               class="w-full border rounded-lg p-2">
                    </div>

                    <button
                        class="bg-red-600 hover:bg-red-700 text-white px-6 py-2 rounded-lg">
                        Save Customer
                    </button>

                </form>

            </div>

        </div>
    </div>
</x-app-layout>